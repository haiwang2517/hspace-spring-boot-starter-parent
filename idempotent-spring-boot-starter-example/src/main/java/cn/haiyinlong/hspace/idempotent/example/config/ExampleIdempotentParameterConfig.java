package cn.haiyinlong.hspace.idempotent.example.config;

import cn.haiyinlong.hspace.idempotent.IdempotentParameterConfig;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
public class ExampleIdempotentParameterConfig implements IdempotentParameterConfig {

  @Override
  public String getToken() {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = attributes.getRequest();
    String token = request.getHeader("token");
    return token;
  }

  @Override
  public String getValue() {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = attributes.getRequest();
    String name = request.getParameter("name");
    return name;
  }
}
