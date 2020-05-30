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

/**
 * Created by superheizai on 2017/7/26.
 */
public class SendResponse {

    private int code;


    private Object info;


    private long offset;
    private String msgId;
    private String topic;
    private int queueOrPartition;

    private String msg;

    public SendResponse(int code, long offset, String msgId, String topic, int queueOrPartition) {

        this.code = code;
        this.offset = offset;
        this.msgId = msgId;
        this.topic = topic;
        this.queueOrPartition = queueOrPartition;
    }


    public SendResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public Object getInfo() {
        return info;
    }

    public long getOffset() {
        return offset;
    }

    public String getMsgId() {
        return msgId;
    }

    public String getTopic() {
        return topic;
    }

    public int getQueueOrPartition() {
        return queueOrPartition;
    }

    public String getMsg() {
        return msg;
    }

    static SendResponse buildSuccessResult(long offset, String msgId, String topic, int queueOrPartition) {
        return new SendResponse(200, offset, msgId, topic, queueOrPartition);
    }

    public static SendResponse SUCCESS = new SendResponse(200, null);
    public static SendResponse FAILURE_NOTRUNNING = new SendResponse(401, "client状态不是running");
    public static SendResponse FAILURE_TIMEOUT = new SendResponse(402, "客户端发送超时");
    public static SendResponse FAILURE_INTERUPRION = new SendResponse(403, "等待线程被中断");

    public static SendResponse buildErrorResult(String msg) {
        return new SendResponse(404, msg);
    }

    public static SendResponse buildErrorResult(int code, String msg) {
        return new SendResponse(code, msg);
    }

    public static SendResponse FAILURE_SLAVE = new SendResponse(405, "slave节点不存在");


    public boolean isSucceed() {
        return this.code == 200;
    }

}

