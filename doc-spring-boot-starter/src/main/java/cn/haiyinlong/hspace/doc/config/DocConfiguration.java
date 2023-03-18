package cn.haiyinlong.hspace.doc.config;

import com.github.xiaoymin.knife4j.core.util.CollectionUtils;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.github.xiaoymin.knife4j.spring.configuration.Knife4jInfoProperties;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.*;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
@Slf4j
@EnableKnife4j
@EnableConfigurationProperties(Knife4jInfoProperties.class)
@Import(BeanValidatorPluginsConfiguration.class)
@EnableSwagger2WebMvc
public class DocConfiguration {

  @Value("${spring.application.name:app}")
  private String applicationName;

  @Value("${knife4j.enable:false}")
  private Boolean knife4jEnable;

  @Value("${server.port}")
  private Integer serverPort;

  @Autowired private Knife4jInfoProperties knife4jInfoProperties;

  @Bean(value = "defaultApi")
  @Order(Integer.MIN_VALUE)
  public Docket defaultApi() {

    printDocInfo();
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        .select()
        .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
        .paths(PathSelectors.any())
        .build()
        .securityContexts(CollectionUtils.newArrayList(securityContext()))
        .securitySchemes(CollectionUtils.newArrayList(apiKey()));
  }

  private void printDocInfo() {
    try {
      String hostAddress = InetAddress.getLocalHost().getHostAddress();
      log.info(
          "\n----------------------------------------------------------\n\t"
              + "Application '{}' is running! Access Doc URLs:\t http://{}:{}/doc.html\n"
              + "knife4j.openapi.title: {} \n"
              + "knife4j.openapi.description: {} \n"
              + "knife4j.enable: {} \n"
              + "----------------------------------------------------------",
          applicationName,
          hostAddress,
          serverPort,
          knife4jInfoProperties.getTitle(),
          knife4jInfoProperties.getDescription(),
          knife4jEnable);
    } catch (Exception e) {

    }
  }

  private ApiInfo apiInfo() {
    // 文档描述
    Contact contact =
        new Contact(
            "doc-starter", knife4jInfoProperties.getUrl(), knife4jInfoProperties.getEmail());

    return new ApiInfoBuilder()
        .title(knife4jInfoProperties.getTitle())
        .description(knife4jInfoProperties.getDescription())
        .termsOfServiceUrl(knife4jInfoProperties.getTermsOfServiceUrl())
        .contact(contact)
        .license(knife4jInfoProperties.getLicense())
        .licenseUrl(knife4jInfoProperties.getLicenseUrl())
        .version(knife4jInfoProperties.getVersion())
        .build();
  }

  private ApiKey apiKey() {
    return new ApiKey("BearerToken", "Authorization", "header");
  }

  private SecurityContext securityContext() {
    return SecurityContext.builder()
        .securityReferences(defaultAuth())
        .forPaths(PathSelectors.regex("/.*"))
        .build();
  }

  List<SecurityReference> defaultAuth() {
    AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
    authorizationScopes[0] = authorizationScope;
    return CollectionUtils.newArrayList(new SecurityReference("BearerToken", authorizationScopes));
  }

  /**
   * 因spingboot启动异常需要添加自定义bean来解决
   *
   * @return
   */
  @Bean
  public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(
      WebEndpointsSupplier webEndpointsSupplier,
      ServletEndpointsSupplier servletEndpointsSupplier,
      ControllerEndpointsSupplier controllerEndpointsSupplier,
      EndpointMediaTypes endpointMediaTypes,
      CorsEndpointProperties corsProperties,
      WebEndpointProperties webEndpointProperties,
      Environment environment) {
    List<ExposableEndpoint<?>> allEndpoints = new ArrayList();
    Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
    allEndpoints.addAll(webEndpoints);
    allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
    allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
    String basePath = webEndpointProperties.getBasePath();
    EndpointMapping endpointMapping = new EndpointMapping(basePath);
    boolean shouldRegisterLinksMapping =
        this.shouldRegisterLinksMapping(webEndpointProperties, environment, basePath);
    return new WebMvcEndpointHandlerMapping(
        endpointMapping,
        webEndpoints,
        endpointMediaTypes,
        corsProperties.toCorsConfiguration(),
        new EndpointLinksResolver(allEndpoints, basePath),
        shouldRegisterLinksMapping,
        null);
  }

  private boolean shouldRegisterLinksMapping(
      WebEndpointProperties webEndpointProperties, Environment environment, String basePath) {
    return webEndpointProperties.getDiscovery().isEnabled()
        && (StringUtils.hasText(basePath)
            || ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
  }
}
