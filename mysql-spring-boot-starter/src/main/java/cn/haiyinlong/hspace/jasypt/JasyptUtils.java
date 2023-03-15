package cn.haiyinlong.hspace.jasypt;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

/**
 * Jasypt 工具<br>
 * 启动命令或配置文件在指定 <br>
 * <li>jasypt.encryptor.algorithm=PBEWithMD5AndDES
 * <li>jasypt.encryptor.iv-generator-classname=org.jasypt.iv.NoIvGenerator
 * <li>jasypt.encryptor.password=hello
 */
public final class JasyptUtils {

  /**
   * 加密
   *
   * @param password
   * @param value
   * @return
   */
  public static String encrypt(String password, String value) {
    PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
    SimpleStringPBEConfig config = getConfig(password);
    encryptor.setConfig(config);
    return encryptor.encrypt(value);
  }

  /**
   * 解密
   *
   * @param password
   * @param value
   * @return
   */
  public static String decrypt(String password, String value) {
    PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
    SimpleStringPBEConfig config = getConfig(password);
    encryptor.setConfig(config);
    return encryptor.decrypt(value);
  }

  private static SimpleStringPBEConfig getConfig(String password) {
    return getConfig(password, "PBEWithMD5AndDES");
  }

  private static SimpleStringPBEConfig getConfig(String password, String algorithm) {
    return getConfig(password, algorithm, "org.jasypt.iv.NoIvGenerator");
  }

  private static SimpleStringPBEConfig getConfig(
      String password, String algorithm, String ivGeneratorClassName) {
    SimpleStringPBEConfig config = new SimpleStringPBEConfig();
    config.setPassword(password);
    config.setAlgorithm(algorithm);
    config.setKeyObtentionIterations("1000");
    config.setPoolSize("1");
    config.setProviderName("SunJCE");
    config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
    config.setIvGeneratorClassName(ivGeneratorClassName);
    config.setStringOutputType("base64");
    return config;
  }
}
