package cn.haiyinlong.hspace.uid.example.uidgeneratorspringbootstarterexample;

import cn.haiyinlong.hspace.uid.UidGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootTest
@EnableTransactionManagement
class UidGeneratorSpringBootStarterExampleApplicationTests {

  @Autowired private UidGenerator uidGenerator;

  @Test
  void contextLoads() {
    long uid = uidGenerator.getUID();
    uidGenerator.parseUID(uid);
    System.out.println(uid);
  }
}
