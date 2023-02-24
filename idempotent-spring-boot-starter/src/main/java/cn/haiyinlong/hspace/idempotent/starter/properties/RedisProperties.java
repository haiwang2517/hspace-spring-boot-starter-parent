package cn.haiyinlong.hspace.idempotent.starter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.ObjectUtils;

@Data
@ConfigurationProperties(prefix = "hspace.idempotent.token")
public class RedisProperties {

  /** idempotent token key 命名规范 */
  private String key;
  /** key 失效时间,分钟 */
  private Long timeout;

  private static final String IDEMPOTENT_TOKEN_PREFIX = "hspace:idempotent:";

  /**
   * 组装 Token Key
   *
   * @param token
   * @return
   */
  public String assemblerTokenKey(String token) {
    if (ObjectUtils.isEmpty(key)) {
      return IDEMPOTENT_TOKEN_PREFIX + token;
    }
    return key + token;
  }
}
