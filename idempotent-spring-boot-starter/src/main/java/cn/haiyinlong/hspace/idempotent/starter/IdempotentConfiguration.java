package cn.haiyinlong.hspace.idempotent.starter;

import cn.haiyinlong.hspace.idempotent.starter.aop.IdempotentAop;
import cn.haiyinlong.hspace.idempotent.starter.properties.RedisProperties;
import cn.haiyinlong.hspace.idempotent.starter.service.IdempotentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class IdempotentConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public IdempotentService idempotentService(
      StringRedisTemplate stringRedisTemplate, RedisProperties redisProperties) {
    log.info("config hspace IdempotentService");
    return new IdempotentService(stringRedisTemplate, redisProperties);
  }

  @Bean
  @ConditionalOnMissingBean
  public IdempotentParameterConfig idempotentParameterConfig() {
    log.info("config hspace idempotentParameterConfig");
    return new IdempotentParameterConfig() {
      @Override
      public String getToken() {
        return null;
      }

      @Override
      public String getValue() {
        return null;
      }
    };
  }

  @Bean
  @ConditionalOnMissingBean
  public IdempotentAop idempotentAop(
      IdempotentService idempotentService, IdempotentParameterConfig idempotentParameterConfig) {
    log.info("config hspace idempotentAop");
    return new IdempotentAop(idempotentService, idempotentParameterConfig);
  }
}
