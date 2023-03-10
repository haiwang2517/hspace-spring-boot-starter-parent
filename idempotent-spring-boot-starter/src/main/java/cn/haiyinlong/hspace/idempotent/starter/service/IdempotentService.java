package cn.haiyinlong.hspace.idempotent.starter.service;

import cn.haiyinlong.hspace.idempotent.starter.properties.IdempotentRedisProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class IdempotentService {
  final StringRedisTemplate redisTemplate;

  final IdempotentRedisProperties idempotentRedisProperties;

  public String generateToken(String value) {
    // 实例化生成 ID 工具对象
    String token = UUID.randomUUID().toString();
    // 设置存入 Redis 的 Key
    String key = idempotentRedisProperties.assemblerTokenKey(token);
    // 存储 Token 到 Redis，且设置过期时间为5分钟
    Long timeout = Optional.ofNullable(idempotentRedisProperties.getTimeout()).orElse(5L);
    redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MINUTES);
    // 返回 Token
    return token;
  }

  public boolean validToken(String token, String value) {
    if (ObjectUtils.isEmpty(token)) {
      return false;
    }
    // 设置 Lua 脚本，其中 KEYS[1] 是 key，KEYS[2] 是 value
    String script =
        "if redis.call('get', KEYS[1]) == KEYS[2] then return redis.call('del', KEYS[1]) else return 0 end";
    RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
    // 根据 Key 前缀拼接 Key
    String key = idempotentRedisProperties.assemblerTokenKey(token);
    // 执行 Lua 脚本
    Long result = redisTemplate.execute(redisScript, Arrays.asList(key, value));
    // 根据返回结果判断是否成功成功匹配并删除 Redis 键值对，若果结果不为空和0，则验证通过
    if (result != null && result != 0L) {
      return true;
    }
    return false;
  }
}
