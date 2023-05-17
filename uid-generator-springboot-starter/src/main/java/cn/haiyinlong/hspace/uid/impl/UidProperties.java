package cn.haiyinlong.hspace.uid.impl;

import cn.haiyinlong.hspace.uid.utils.DateUtils;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

@ConfigurationProperties(prefix = "hspace.uid")
@Data
public class UidProperties {

  /** 时间占用位数(单位秒) */
  private int timeBits = 28;

  /** 机器码ID占用位数 */
  private int workerBits = 22;

  /** 序列号占用位数 */
  private int seqBits = 13;

  /** 时间基点, YYYY-mm-dd */
  private String epochStr = "2016-05-20";

  /** 时间基点毫秒数 */
  protected long epochSeconds = TimeUnit.MILLISECONDS.toSeconds(1463673600000L);

  /** 是否容忍时钟回拨, 默认:true */
  private boolean enableBackward = true;

  /** 时钟回拨最长容忍时间（秒） */
  private long maxBackwardSeconds = 1L;

  /**
   * RingBuffer size扩容参数, 可提高UID生成的吞吐量. <br>
   * 默认:3， 原bufferSize=8192, 扩容后bufferSize= 8192 << 3 = 65536
   */
  private Integer boostPower = null;
  /** 指定何时向RingBuffer中填充UID, 取值为百分比(0, 100), 默认为50 */
  private Integer paddingFactor = null;
  /** 另外一种RingBuffer填充时机, 在Schedule线程中, 周期性检查填充, 默认:不配置此项 */
  private Long scheduleInterval = null;

  public void setEpochStr(String epochStr) {
    if (StringUtils.isNotBlank(epochStr)) {
      this.epochStr = epochStr;
      this.epochSeconds =
          TimeUnit.MILLISECONDS.toSeconds(DateUtils.parseByDayPattern(epochStr).getTime());
    }
  }
}
