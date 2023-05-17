## Redis 序列化配置

__默认配置Jackson2JsonRedisSerializer__

```java
 Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer=
        new Jackson2JsonRedisSerializer<>(Object.class);
```


