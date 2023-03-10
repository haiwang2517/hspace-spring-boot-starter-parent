package cn.haiyinlong.hspace.idempotent.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootTest
class IdempotentControllerTest {

  @Autowired WebApplicationContext webApplicationContext;

  @Test
  void idempotentTest() throws Exception {
    MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    // 获取接口返回的token值
    String token =
        mockMvc
            .perform(MockMvcRequestBuilders.get("/getToken").accept(MediaType.TEXT_HTML))
            .andReturn()
            .getResponse()
            .getContentAsString();
    log.info("token={}", token);

    // 调用验证接口
    List<Integer> forList = new ArrayList(150);
    for (int i = 1; i <= 10; i++) {
      forList.add(i);
    }
    forList.stream()
        .parallel()
        .forEach(
            forItem -> {
              try {
                String result =
                    mockMvc
                        .perform(
                            MockMvcRequestBuilders.get("/getToken/" + token)
                                .accept(MediaType.TEXT_HTML))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
                log.info("第{}次请求,请求结果{}", forItem, result);
              } catch (Exception e) {
                throw new RuntimeException(e);
              }
            });
  }
}
