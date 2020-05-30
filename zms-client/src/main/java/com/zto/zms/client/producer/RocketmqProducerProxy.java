/*
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

package com.zto.zms.client.producer;

import com.zto.zms.client.common.SLA;
import com.zto.zms.common.ZmsEnv;
import com.zto.zms.common.ZmsException;
import com.zto.zms.client.common.ZmsMessage;
import com.zto.zms.client.config.ProducerConfig;
import com.zto.zms.client.consumer.MsgConsumedStatus;
import com.zto.zms.logger.ZmsLogger;
import com.zto.zms.metadata.ZmsMetadata;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.remoting.exception.RemotingTimeoutException;
import org.slf4j.Logger;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Created by superheizai on 2017/7/26.
 */
public class RocketmqProducerProxy extends ZmsProducerProxy {

    public static final Logger logger = ZmsLogger.log;

    private static MessageQueueSelector hashSelector = (mqs, msg, arg) -> {
        int id = msg.getKeys().hashCode() % mqs.size();
        if (id < 0) {
            return mqs.get(-id);
        } else {
            return mqs.get(id);

        }
    };

    DefaultMQProducer producer;

    public RocketmqProducerProxy(ZmsMetadata metadata, SLA sla, String name) {
        super(metadata, sla, name);
        this.instanceName = name;
        start();
    }

    public RocketmqProducerProxy(ZmsMetadata metadata, SLA sla, String name, Properties properties) {
        super(metadata, sla, name, properties);
        this.instanceName = name;
        start();
    }


    @Override
    public void startProducer() {
        producer = new DefaultMQProducer("zms_" + System.currentTimeMillis());
        if (metadata.isGatedLaunch()) {
            producer.setNamesrvAddr(metadata.getGatedCluster().getBootAddr());
            producer.setClientIP("producer-client-id-" + metadata.getGatedCluster().getClusterName() + "-" + ZmsEnv.ZMS_IP);
        } else {
            producer.setNamesrvAddr(metadata.getClusterMetadata().getBootAddr());
            producer.setClientIP("producer-client-id-" + metadata.getClusterMetadata().getClusterName() + "-" + ZmsEnv.ZMS_IP);
        }
        int retries = 2;
        int timeout = 3000;
        if (customizedProperties != null) {
            if (customizedProperties.containsKey(ProducerConfig.RETRIES)) {
                retries = (Integer) (customizedProperties.get(ProducerConfig.RETRIES));
            }
            if (customizedProperties.containsKey(ProducerConfig.TIMEOUT)) {
                timeout = (Integer) customizedProperties.get(ProducerConfig.TIMEOUT);
            }
        }
        producer.setRetryTimesWhenSendFailed(retries);
        producer.setRetryTimesWhenSendAsyncFailed(retries);
        producer.setSendMsgTimeout(timeout);
        producer.setVipChannelEnabled(false);
        try {
            producer.start();
        } catch (MQClientException e) {
            logger.error("producer {} start failed", metadata.getName(), e);
            throw ZmsException.PRODUCER_START_EXCEPTION;
        }
    }

    @Override
    public void shutdownProducer() {
        producer.shutdown();
    }

    @Override
    public SendResponse syncSend(ZmsMessage zmsMessage) {
        if (!running) {
            return SendResponse.FAILURE_NOTRUNNING;
        }
        boolean succeed = false;

        try {
            Message message = new Message(metadata.getName(), zmsMessage.getTags(), zmsMessage.getKey(), zmsMessage.getPayload());

            if (zmsMessage.getProperties() != null) {
                message.getProperties().putAll(zmsMessage.getProperties());
            }
            if (zmsMessage.getDelayLevel() >= MsgConsumedStatus.RETRY_1S.getLevel() && zmsMessage.getDelayLevel() <= MsgConsumedStatus.RETRY_2H.getLevel()) {
                message.setDelayTimeLevel(zmsMessage.getDelayLevel());
            }
            zmsMetrics.msgBody().markSize(zmsMessage.getPayload().length);

            SendResult send;
            long startTime = System.currentTimeMillis();
            if (StringUtils.isEmpty(message.getKeys())) {
                send = producer.send(message);

            } else {
                send = producer.send(message, hashSelector, message.getKeys());
            }
            if (send.getSendStatus().equals(SendStatus.SEND_OK)) {
                long duration = System.currentTimeMillis() - startTime;
                zmsMetrics.sendCostRate().update(duration, TimeUnit.MILLISECONDS);
                succeed = true;
                zmsMetrics.getDistribution().markTime(duration);
                return SendResponse.buildSuccessResult(send.getQueueOffset(), send.getOffsetMsgId(), send.getMessageQueue().getTopic(), send.getMessageQueue().getQueueId());
            } else if (send.getSendStatus().equals(SendStatus.FLUSH_DISK_TIMEOUT) || send.getSendStatus().equals(SendStatus.FLUSH_SLAVE_TIMEOUT)) {
                zmsMetrics.messageFailureRate().mark();
                logger.error("syncSend topic {} timeout for {} ", metadata.getName(), send.getSendStatus().name());
                return SendResponse.FAILURE_TIMEOUT;
            } else {
                zmsMetrics.messageFailureRate().mark();
                logger.error("syncSend topic {} failed slave not exist ", metadata.getName());
                return SendResponse.FAILURE_TIMEOUT;
            }
        } catch (MQClientException e) {
            logger.error("send failed for ", e);
            return SendResponse.buildErrorResult("syncSend message MQClientException: " + e.getMessage());
        } catch (RemotingTimeoutException e) {
            logger.error("send failed for ", e);
            return SendResponse.FAILURE_TIMEOUT;

        } catch (RemotingException e) {
            logger.error("send failed for ", e);
            return SendResponse.buildErrorResult("syncSend message RemotingException: " + e.getMessage());
        } catch (MQBrokerException e) {
            logger.error("send failed for ", e);

            return SendResponse.buildErrorResult("syncSend message MQBrokerException: " + e.getMessage());
        } catch (InterruptedException e) {
            logger.error("send failed for ", e);
            logger.error("produce syncSend and wait interuptted", e);
            return SendResponse.FAILURE_INTERUPRION;
        } finally {
            if (succeed) {
                zmsMetrics.messageSuccessRate().mark();
            } else {
                zmsMetrics.messageFailureRate().mark();

            }
        }
    }

    @Override
    public void asyncSend(ZmsMessage zmsMessage, ZmsCallBack zmsCallBack) {
        Message message = new Message(metadata.getName(), zmsMessage.getTags(), zmsMessage.getKey(), zmsMessage.getPayload());

        if (zmsMessage.getProperties() != null) {
            message.getProperties().putAll(zmsMessage.getProperties());
        }

        if (zmsMessage.getDelayLevel() >= MsgConsumedStatus.RETRY_1S.getLevel() && zmsMessage.getDelayLevel() <= MsgConsumedStatus.RETRY_2H.getLevel()) {
            message.setDelayTimeLevel(zmsMessage.getDelayLevel());
        }

        long startTime = System.currentTimeMillis();
        zmsMetrics.msgBody().markSize(zmsMessage.getPayload().length);
        try {
            producer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult send) {
                    long duration = System.currentTimeMillis() - startTime;
                    zmsMetrics.sendCostRate().update(duration, TimeUnit.MILLISECONDS);
                    zmsMetrics.messageSuccessRate().mark();
                    zmsMetrics.getDistribution().markTime(duration);
                    zmsCallBack.onResult(SendResponse.buildSuccessResult(send.getQueueOffset(), send.getOffsetMsgId(), send.getMessageQueue().getTopic(), send.getMessageQueue().getQueueId()));
                }

                @Override
                public void onException(Throwable e) {
                    zmsMetrics.messageFailureRate().mark();
                    logger.error("aysnc send failed for ", e);
                    zmsCallBack.onException(e);

                }
            });
        } catch (MQClientException | RemotingException | InterruptedException e) {
            logger.error("aysnc send failed for ", e);
        }
    }

    @Override
    public void oneway(ZmsMessage zmsMessage) {
        Message message = new Message(metadata.getName(), zmsMessage.getTags(), zmsMessage.getKey(), 0, zmsMessage.getPayload(), false);
        zmsMetrics.msgBody().markSize(zmsMessage.getPayload().length);
        try {
            producer.send(message);
        } catch (MQClientException | RemotingException | MQBrokerException | InterruptedException e) {
            logger.warn("exception was ignored for oneway", e);
        }
    }


}

