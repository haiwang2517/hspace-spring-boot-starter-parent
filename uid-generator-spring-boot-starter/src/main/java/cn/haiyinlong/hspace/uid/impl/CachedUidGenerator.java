/*
 * Copyright (c) 2017 Baidu, Inc. All Rights Reserve.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.haiyinlong.hspace.uid.impl;

import cn.haiyinlong.hspace.uid.BitsAllocator;
import cn.haiyinlong.hspace.uid.UidGenerator;
import cn.haiyinlong.hspace.uid.buffer.BufferPaddingExecutor;
import cn.haiyinlong.hspace.uid.buffer.RejectedPutBufferHandler;
import cn.haiyinlong.hspace.uid.buffer.RejectedTakeBufferHandler;
import cn.haiyinlong.hspace.uid.buffer.RingBuffer;
import cn.haiyinlong.hspace.uid.exception.UidGenerateException;
import cn.haiyinlong.hspace.uid.worker.WorkerIdAssigner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a cached implementation of {@link UidGenerator} extends from {@link
 * DefaultUidGenerator}, based on a lock free {@link RingBuffer}
 *
 * <p>The spring properties you can specified as below:<br>
 * <li><b>boostPower:</b> RingBuffer size boost for a power of 2, Sample: boostPower is 3, it means
 *     the buffer size will be <code>({@link BitsAllocator#getMaxSequence()} + 1) &lt;&lt;
 *                        {@link #boostPower}</code>, Default as {@value #DEFAULT_BOOST_POWER}
 * <li><b>paddingFactor:</b> Represents a percent value of (0 - 100). When the count of rest
 *     available UIDs reach the threshold, it will trigger padding buffer. Default as{@link
 *     RingBuffer#DEFAULT_PADDING_PERCENT} Sample: paddingFactor=20, bufferSize=1000 ->
 *     threshold=1000 * 20 /100, padding buffer will be triggered when tail-cursor<threshold
 * <li><b>scheduleInterval:</b> Padding buffer in a schedule, specify padding buffer interval, Unit
 *     as second
 * <li><b>rejectedPutBufferHandler:</b> Policy for rejected put buffer. Default as discard put
 *     request, just do logging
 * <li><b>rejectedTakeBufferHandler:</b> Policy for rejected take buffer. Default as throwing up an
 *     exception
 *
 * @author yutianbao
 */
public class CachedUidGenerator extends DefaultUidGenerator implements DisposableBean {
  private static final Logger LOGGER = LoggerFactory.getLogger(CachedUidGenerator.class);
  private static final int DEFAULT_BOOST_POWER = 3;

  /** Spring properties */
  private int boostPower = DEFAULT_BOOST_POWER;

  private int paddingFactor = RingBuffer.DEFAULT_PADDING_PERCENT;
  private Long scheduleInterval;

  /**
   * 拒绝策略: 当环已满, 无法继续填充时 <br>
   * 默认无需指定, 将丢弃Put操作, 仅日志记录. 如有特殊需求, 请实现RejectedPutBufferHandler接口(支持Lambda表达式)并以@Autowired方式注入
   */
  @Autowired(required = false)
  private RejectedPutBufferHandler rejectedPutBufferHandler;

  /**
   * 拒绝策略: 当环已空, 无法继续获取时<br>
   * 默认无需指定, 将记录日志, 并抛出UidGenerateException异常. 如有特殊需求,
   * 请实现RejectedTakeBufferHandler接口(支持Lambda表达式)并以@Autowired方式注入
   */
  @Autowired(required = false)
  private RejectedTakeBufferHandler rejectedTakeBufferHandler;

  /** RingBuffer */
  private RingBuffer ringBuffer;

  private BufferPaddingExecutor bufferPaddingExecutor;

  public CachedUidGenerator(UidProperties uidProperties, WorkerIdAssigner workerIdAssigner) {
    super(uidProperties, workerIdAssigner);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    // initialize workerId & bitsAllocator
    super.afterPropertiesSet();
    // set boostPower
    this.afterCachedPropertiesSet();
    // initialize RingBuffer & RingBufferPaddingExecutor
    this.initRingBuffer();
    LOGGER.info("Initialized RingBuffer successfully.");
  }

  private void afterCachedPropertiesSet() throws Exception {
    if (null != uidProperties.getBoostPower() && uidProperties.getBoostPower() > 0) {
      this.boostPower = uidProperties.getBoostPower();
    }
    if (null != uidProperties.getPaddingFactor() && uidProperties.getPaddingFactor() > 0) {
      this.paddingFactor = uidProperties.getPaddingFactor();
    }
    if (null != uidProperties.getScheduleInterval() && uidProperties.getScheduleInterval() > 0) {
      this.scheduleInterval = uidProperties.getScheduleInterval();
    }

    LOGGER.info("Set RingBuffer Properties.");
  }

  @Override
  public long getUID() {
    try {
      return ringBuffer.take();
    } catch (Exception e) {
      LOGGER.error("Generate unique id exception. ", e);
      throw new UidGenerateException(e);
    }
  }

  @Override
  public String parseUID(long uid) {
    return super.parseUID(uid);
  }

  @Override
  public void destroy() throws Exception {
    bufferPaddingExecutor.shutdown();
  }

  /**
   * Get the UIDs in the same specified second under the max sequence
   *
   * @param currentSecond
   * @return UID list, size of {@link BitsAllocator#getMaxSequence()} + 1
   */
  protected List<Long> nextIdsForOneSecond(long currentSecond) {
    // Initialize result list size of (max sequence + 1)
    int listSize = (int) bitsAllocator.getMaxSequence() + 1;
    List<Long> uidList = new ArrayList<>(listSize);

    // Allocate the first sequence of the second, the others can be calculated with the offset
    long firstSeqUid =
        bitsAllocator.allocate(currentSecond - uidProperties.getEpochSeconds(), workerId, 0L);
    for (int offset = 0; offset < listSize; offset++) {
      uidList.add(firstSeqUid + offset);
    }

    return uidList;
  }

  /** Initialize RingBuffer & RingBufferPaddingExecutor */
  private void initRingBuffer() {
    // initialize RingBuffer
    int bufferSize = ((int) bitsAllocator.getMaxSequence() + 1) << boostPower;
    this.ringBuffer = new RingBuffer(bufferSize, paddingFactor);
    LOGGER.info("Initialized ring buffer size:{}, paddingFactor:{}", bufferSize, paddingFactor);

    // initialize RingBufferPaddingExecutor
    boolean usingSchedule = (scheduleInterval != null);
    this.bufferPaddingExecutor =
        new BufferPaddingExecutor(ringBuffer, this::nextIdsForOneSecond, usingSchedule);
    if (usingSchedule) {
      bufferPaddingExecutor.setScheduleInterval(scheduleInterval);
    }

    LOGGER.info(
        "Initialized BufferPaddingExecutor. Using schdule:{}, interval:{}",
        usingSchedule,
        scheduleInterval);

    // set rejected put/take handle policy
    this.ringBuffer.setBufferPaddingExecutor(bufferPaddingExecutor);
    if (rejectedPutBufferHandler != null) {
      this.ringBuffer.setRejectedPutHandler(rejectedPutBufferHandler);
    }
    if (rejectedTakeBufferHandler != null) {
      this.ringBuffer.setRejectedTakeHandler(rejectedTakeBufferHandler);
    }

    // fill in all slots of the RingBuffer
    bufferPaddingExecutor.paddingBuffer();

    // start buffer padding threads
    bufferPaddingExecutor.start();
  }
}
