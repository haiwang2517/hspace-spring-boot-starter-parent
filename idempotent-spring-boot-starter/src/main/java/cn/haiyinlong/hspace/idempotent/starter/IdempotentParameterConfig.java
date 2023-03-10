package cn.haiyinlong.hspace.idempotent.starter;

import cn.haiyinlong.hspace.idempotent.starter.exception.IdempotentException;

public interface IdempotentParameterConfig {
  /**
   * 获取Token
   *
   * @return
   */
  String getToken() throws IdempotentException;

  /**
   * 获取Token 存储时的value
   *
   * @return
   */
  String getValue() throws IdempotentException;
}
