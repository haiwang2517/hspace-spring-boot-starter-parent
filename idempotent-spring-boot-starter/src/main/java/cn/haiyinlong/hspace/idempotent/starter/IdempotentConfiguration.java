package cn.haiyinlong.hspace.idempotent.starter;

import cn.haiyinlong.hspace.idempotent.starter.aop.IdempotentAop;
import cn.haiyinlong.hspace.idempotent.starter.exception.IdempotentException;
import cn.haiyinlong.hspace.idempotent.starter.properties.IdempotentRedisProperties;
import cn.haiyinlong.hspace.idempotent.starter.service.IdempotentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
@Configuration
@EnableConfigurationProperties(IdempotentRedisProperties.class)
public class IdempotentConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public IdempotentService idempotentService(
      StringRedisTemplate stringRedisTemplate,
      IdempotentRedisProperties idempotentRedisProperties) {
    log.info("config hspace IdempotentService");
    return new IdempotentService(stringRedisTemplate, idempotentRedisProperties);
  }

  @Bean
  @ConditionalOnMissingBean
  public IdempotentParameterConfig idempotentParameterConfig() {
    log.info("config hspace idempotentParameterConfig");
    return new IdempotentParameterConfig() {
      @Override
      public String getToken() throws IdempotentException {
        throw new IdempotentException("not get token, please implement IdempotentParameterConfig");
      }

      @Override
      public String getValue() throws IdempotentException {
        throw new IdempotentException("not get token, please implement IdempotentParameterConfig");
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
