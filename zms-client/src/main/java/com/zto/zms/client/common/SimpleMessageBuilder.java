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

package com.zto.zms.client.common;

/**
 * Created by superheizai on 2017/8/7.
 */
public class SimpleMessageBuilder {


    private SimpleMessage message = new SimpleMessage();


    public static SimpleMessageBuilder newInstance() {
        return new SimpleMessageBuilder();
    }

    public SimpleMessageBuilder buildKey(String key) {
        message.setKey(key);
        return this;
    }

    public SimpleMessageBuilder buildTags(String tags) {
        message.setTags(tags);
        return this;
    }

    public SimpleMessageBuilder buildPayload(byte[] payload) {
        message.setPayload(payload);
        return this;
    }


    public SimpleMessageBuilder buildDelayLevel(int delayLevel) {
        message.setDelayLevel(delayLevel);
        return this;
    }

    public SimpleMessage build() {
        return message;
    }
}
