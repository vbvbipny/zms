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

package com.zto.zms.zookeeper;

import com.zto.zms.common.ZmsConst;
import com.zto.zms.common.ZmsException;
import com.zto.zms.logger.ZmsLogger;
import kafka.utils.ZKStringSerializer$;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

/**
 * Created by superheizai on 2017/7/26.
 */
public class RouterManager {

    public static final Logger logger = ZmsLogger.log;

    public ZmsZkClient zkClient;

    private RouterManager() {
        String zmsServer = System.getProperty(ZmsConst.ZK.ZMS_STARTUP_PARAM);
        String effectiveParam = "ZMS_STARTUP_PARAM";

        if (StringUtils.isEmpty(zmsServer)) {
            throw ZmsException.NO_ZK_EXCEPTION;
        }

        zkClient = new ZmsZkClient(zmsServer, 20 * 1000, 10 * 1000, ZKStringSerializer$.MODULE$);
        logger.info("zk connected to {} by parameter {}", zmsServer, effectiveParam);
    }


    private static class InstanceHolder {
        private static RouterManager routerManager = new RouterManager();
    }


    public static RouterManager getInstance() {
        return InstanceHolder.routerManager;
    }


    public static ZmsZkClient getZkInstance() {
        return InstanceHolder.routerManager.zkClient;
    }

    public void shutdown() {
        zkClient.close();
        logger.info("routerManager shutdown");
    }

}

