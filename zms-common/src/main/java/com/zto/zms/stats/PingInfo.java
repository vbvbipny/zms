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

package com.zto.zms.stats;

/**
 * Created by superheizai on 2017/9/19.
 */
public class PingInfo {

    private long startTime = System.currentTimeMillis();

    private String source;

    private String target;

    //    发送成功的ping
    private int succeedSend;

    //    发送超时的send
    private int timeoutSend;

    //    没有发出去ping命令
    private int failSendOut;

    //    丢包率
    private Double dataLost;

    private Double min;

    private Double max;

    private Double avg;

    private Double stdev;


    public int getSucceedSend() {
        return succeedSend;
    }

    public void setSucceedSend(int succeedSend) {
        this.succeedSend = succeedSend;
    }

    public int getTimeoutSend() {
        return timeoutSend;
    }

    public void setTimeoutSend(int timeoutSend) {
        this.timeoutSend = timeoutSend;
    }

    public int getFailSendOut() {
        return failSendOut;
    }

    public void setFailSendOut(int failSendOut) {
        this.failSendOut = failSendOut;
    }

    public Double getDataLost() {
        return dataLost;
    }

    public void setDataLost(Double dataLost) {
        this.dataLost = dataLost;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Double getAvg() {
        return avg;
    }

    public void setAvg(Double avg) {
        this.avg = avg;
    }

    public Double getStdev() {
        return stdev;
    }

    public void setStdev(Double stdev) {
        this.stdev = stdev;
    }

    public void succeedCount() {
        succeedSend++;
    }

    public void timeoutCount() {
        timeoutSend++;
    }

    public void failSendCount() {

        failSendOut++;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}

