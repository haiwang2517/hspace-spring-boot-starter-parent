package cn.haiyinlong.hspace.mysql.example.dao;

import cn.haiyinlong.hspace.jasypt.JasyptUtils;
import org.junit.jupiter.api.Test;

public class JasyptTest {

  @Test
  public void code() {
    String encrypt = JasyptUtils.encrypt("hello", "ScudPower");
    String decrypt = JasyptUtils.decrypt("hello", encrypt);
    System.out.println(encrypt + "  == " + decrypt);
  }
}
