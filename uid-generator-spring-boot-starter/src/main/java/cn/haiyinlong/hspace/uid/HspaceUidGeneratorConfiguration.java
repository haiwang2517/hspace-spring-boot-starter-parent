package cn.haiyinlong.hspace.uid;

import cn.haiyinlong.hspace.uid.impl.CachedUidGenerator;
import cn.haiyinlong.hspace.uid.impl.DefaultUidGenerator;
import cn.haiyinlong.hspace.uid.impl.UidProperties;
import cn.haiyinlong.hspace.uid.worker.DisposableWorkerIdAssigner;
import cn.haiyinlong.hspace.uid.worker.WorkerIdAssigner;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@ConditionalOnClass({DefaultUidGenerator.class, CachedUidGenerator.class})
@MapperScan({"cn.haiyinlong.hspace.uid.worker.dao"})
@EnableConfigurationProperties(UidProperties.class)
public class HspaceUidGeneratorConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public DefaultUidGenerator defaultUidGenerator(
      UidProperties uidProperties, WorkerIdAssigner workerIdAssigner) {
    return new DefaultUidGenerator(uidProperties, workerIdAssigner);
  }

  @Bean
  @Primary
  @ConditionalOnMissingBean
  public CachedUidGenerator cachedUidGenerator(
      UidProperties uidProperties, WorkerIdAssigner workerIdAssigner) {
    return new CachedUidGenerator(uidProperties, workerIdAssigner);
  }

  @Bean
  @ConditionalOnMissingBean
  public WorkerIdAssigner disposableWorkerIdAssigner() {
    return new DisposableWorkerIdAssigner();
  }
}
