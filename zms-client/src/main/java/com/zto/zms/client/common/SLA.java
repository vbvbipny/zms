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

import com.zto.zms.common.ZmsConst;

import java.util.Properties;

/**
 * Created by superheizai on 2017/7/26.
 */
public class SLA {

    public boolean isOrderly = false;

    public static SLA parse(Properties properties) {
        SLA sla = new SLA();

        if (properties.containsKey(ZmsConst.CLIENT_CONFIG.CONSUME_ORDERLY)) {
            sla.setOrderly(Boolean.parseBoolean(properties.getProperty(ZmsConst.CLIENT_CONFIG.CONSUME_ORDERLY)));
        }

        return sla;
    }


    public boolean isOrderly() {
        return isOrderly;
    }

    public void setOrderly(boolean orderly) {
        isOrderly = orderly;
    }

}

