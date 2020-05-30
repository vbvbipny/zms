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

package com.zto.zms.client.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * Created by superheizai on 2017/8/16.
 */
public interface MessageListener {

	RuntimeException RUNTIME_EXCEPTION = new RuntimeException("illegal messageListener type, should correct rocketmqMessageListener or kafkaMessageListener");

	default MsgConsumedStatus onMessage(ConsumeMessage msg) {
		return MsgConsumedStatus.SUCCEED;
	}

	default MsgConsumedStatus onMessage(List<MessageExt> msg) {
		return MsgConsumedStatus.SUCCEED;
	}

	default MsgConsumedStatus onMessage(ConsumerRecord msg) {
		return MsgConsumedStatus.SUCCEED;
	}

	default boolean isEasy() {
		return true;
	}
}

