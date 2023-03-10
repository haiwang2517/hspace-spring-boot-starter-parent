package cn.haiyinlong.hspace.idempotent.example.controller;

import cn.haiyinlong.hspace.idempotent.starter.annotation.IdempotentVerification;
import cn.haiyinlong.hspace.idempotent.starter.service.IdempotentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class IdempotentController {

  IdempotentService idempotentService;

  @GetMapping("/getToken")
  public String getToken() {
    return idempotentService.generateToken("hello");
  }

  @GetMapping("/getToken/{token}")
  public String validToken(@PathVariable("token") String token) {
    return idempotentService.validToken(token, "hello") ? "通过" : "不通过";
  }

  @GetMapping("/annotation")
  @IdempotentVerification
  public String annotationValidToken() {
    // 通过注解验证, "不通过" 时会抛出异常
    return "通过";
  }
}
