## 幂等性

> 基于redis-spring-boot-stater

采用Redis Token 实现幂等性.

![流程](http://blog.haiyinlong.cn/images/java/redisToken.png)

## 使用方法

1. 引入依赖包

```xml

<dependency>
    <groupId>cn.haiyinlong.hspace</groupId>
    <artifactId>idempotent-spring-boot-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

2. 添加获取Token相关配置

```java

@Component
public class ExampleIdempotentParameterConfig implements IdempotentParameterConfig {

    /**
     * 获取前端传过来的幂等性token
     * @return
     */
    @Override
    public String getToken() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String token = request.getHeader("token");
        return token;
    }

    /**
     * 获取幂等性token存下的value,用于判断是否属于自己创建的token
     * @return
     */
    @Override
    public String getValue() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String name = request.getParameter("name");
        return name;
    }
}
```

3. Controller 中添加注解`@IdempotentVerification` 开启幂等性验证

```java
  @GetMapping("/annotation")
@IdempotentVerification
public String annotationValidToken(){
        // 通过注解验证, "不通过" 时会抛出异常
        return"通过";
        }
```

__可以自定义Redis存储Token信息__

```properties
# 设置幂等性存下的Key
hspace.idempotent.token.key=hspace:example:
# 设置幂等性token自动失效时间
hspace.idempotent.token.timeout=1
```

### 注意

> 使用`@IdempotentVerification`注解验证Token时,如果Token不存在就会抛出`IdempotentException`,需要业务代码进行捕获处理.












