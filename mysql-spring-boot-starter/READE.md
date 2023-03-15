## 数据源和 加密解密配置

> https://jiuaidu.com/jianzhan/997563/

实现自定义`加密解密` 参考, 在Docker 和 K8S 的时候加上一下配置,统一

```properties
jasypt.encryptor.algorithm=PBEWithMD5AndDES
jasypt.encryptor.iv-generator-classname=org.jasypt.salt.NoOpIVGenerator
jasypt.encryptor.password=hello
```

