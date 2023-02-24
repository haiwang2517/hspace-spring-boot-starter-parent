package cn.haiyinlong.hspace.idempotent.starter.aop;

import cn.haiyinlong.hspace.idempotent.starter.IdempotentParameterConfig;
import cn.haiyinlong.hspace.idempotent.starter.exception.IdempotentException;
import cn.haiyinlong.hspace.idempotent.starter.service.IdempotentService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class IdempotentAop {

  final IdempotentService idempotentService;
  final IdempotentParameterConfig idempotentParameterConfig;

  public IdempotentAop(
      IdempotentService idempotentService, IdempotentParameterConfig idempotentParameterConfig) {
    this.idempotentService = idempotentService;
    this.idempotentParameterConfig = idempotentParameterConfig;
  }

  @Pointcut(
      "@annotation(cn.haiyinlong.hspace.idempotent.starter.annotation.IdempotentVerification)")
  public void point() {}

  @Before("point()")
  public void before(JoinPoint joinPoint) throws IdempotentException {

    boolean validToken =
        idempotentService.validToken(
            idempotentParameterConfig.getToken(), idempotentParameterConfig.getValue());
    if (!validToken) {
      throw new IdempotentException("重复操作");
    }
  }
}
