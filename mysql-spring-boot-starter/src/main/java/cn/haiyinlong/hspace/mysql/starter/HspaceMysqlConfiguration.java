package cn.haiyinlong.hspace.mysql.starter;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

/** Mysql数据库配置,集成MybatisPlus */
public class HspaceMysqlConfiguration {

  /** 注册分页插件 */
  @Bean
  @Order(Integer.MIN_VALUE)
  @ConditionalOnMissingBean
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    // 单一数据源,指定mysql,如果不设置就会在执行sql时自动判断对应的类型
    interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
    // 设置乐观锁
    interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
    // 防止全表操作,更新和删除
    interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
    return interceptor;
  }
}
