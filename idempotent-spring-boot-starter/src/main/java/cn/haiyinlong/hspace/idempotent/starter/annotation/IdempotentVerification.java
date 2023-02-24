package cn.haiyinlong.hspace.idempotent.starter.annotation;

import java.lang.annotation.*;

/** 幂等性验证注解 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IdempotentVerification {}
