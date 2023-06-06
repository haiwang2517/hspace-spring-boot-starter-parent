package cn.haiyinlong.hspace.redis.example;

import cn.haiyinlong.hspace.redis.example.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisTest {
  @Autowired private RedisTemplate redisTemplate;

  @Test
  public void testSerializer() {
    User user = new User();
    user.setAge(18);
    user.setName("花泽香菜");
    redisTemplate.opsForValue().set("user2", user);
    Object userValue = redisTemplate.opsForValue().get("user2");
    System.out.println(userValue);
  }
}
