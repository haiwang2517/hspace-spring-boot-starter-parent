package cn.haiyinlong.hspace.idempotent.starter;

public interface IdempotentParameterConfig {
  /**
   * 获取Token
   *
   * @return
   */
  String getToken();

  /**
   * 获取Token 存储时的value
   *
   * @return
   */
  String getValue();
}
