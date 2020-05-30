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

package com.zto.zms.utils;

import com.google.common.collect.Maps;
import com.zto.zms.common.ZmsConst;
import com.zto.zms.common.ZmsEnv;
import com.zto.zms.metadata.ConsumerGroupMetadata;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.utils.collection.CollectionUtil;
import org.springside.modules.utils.collection.MapUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by superheizai on 2017/7/27.
 */
public class Utils {


    public static final Logger logger = LoggerFactory.getLogger(Utils.class);

    //    zmsVersion will be generated when build jar with maven, int the MANIFEST.MF
    public static String getZmsVersion() {
        Utils object = new Utils();
        Package objPackage = object.getClass().getPackage();

        String version = objPackage.getImplementationVersion();

        if (!StringUtils.isEmpty(version)) {
            return version;
        }

        version = objPackage.getSpecificationVersion();
        if (!StringUtils.isEmpty(version)) {
            return version;
        }
        return version;
    }

    public static Properties parseProperties(String source) throws IOException {
        Properties properties = new Properties();
        if (source == null || source.isEmpty()) {
            return properties;


        }
        try {
            properties.load(new ByteArrayInputStream(source.getBytes(ZmsConst.CHAR_ENCODING)));
        } catch (IOException e) {
            throw e;
        }
        return properties;
    }

    public static String buildPath(String... args) {
        if (args.length < 1) {
            return "";
        }

        return String.join("/", args);
    }

    public static String separatePath(String path, String... args) {
        return path.replace(String.join("/", args).concat("/"), "");
    }

    public static final String buildName(String name) {
        return ZmsEnv.ZMS_IP + "||" + name + "||" + ZmsEnv.ZMS_VERSION + "||" + LocalDateTime.now() + "||" + ThreadLocalRandom.current().nextInt(100000);
    }


    public static <T> String toString(List<T> lists) {
        if (CollectionUtil.isEmpty(lists)) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder("[ ");
        for (T object : lists) {
            stringBuilder.append(object.toString());
            stringBuilder.append(",");
        }

        return stringBuilder.substring(0, stringBuilder.length() - 1) + " ]";


    }


    public static <T, V> String toString(Map<T, V> maps) {

        if (MapUtil.isEmpty(maps)) {
            return "";
        }


        StringBuilder stringBuilder = new StringBuilder("[ ");

        for (Map.Entry<T, V> tvEntry : maps.entrySet()) {

            stringBuilder.append(tvEntry.getKey().toString());
            stringBuilder.append(":");
            stringBuilder.append(tvEntry.getValue().toString());
            stringBuilder.append(",");
        }

        return stringBuilder.substring(0, stringBuilder.length() - 1) + " ]";


    }


    public static void main(String[] args) {

        Map<ConsumerGroupMetadata, ConsumerGroupMetadata> maps = Maps.newHashMap();

        ConsumerGroupMetadata t1 = new ConsumerGroupMetadata();
        t1.setConsumeFrom("early");
        t1.setBroadcast("true");
        t1.setName("t1");
        t1.setType("tttt");
        ConsumerGroupMetadata t2 = new ConsumerGroupMetadata();
        t2.setConsumeFrom("early");
        t2.setBroadcast("true");
        t2.setName("t2");
        t2.setType("ttt2t");
        ConsumerGroupMetadata t3 = new ConsumerGroupMetadata();
        t3.setConsumeFrom("early");
        t3.setBroadcast("true");
        t3.setName("t3");
        t3.setType("tt3tt");

        maps.put(t1, t1);
        maps.put(t2, t1);
        maps.put(t3, t2);
        System.out.println(toString(maps));

    }


    public static String abbrev(TimeUnit unit) {
        switch (unit) {
            case NANOSECONDS:
                return "ns";
            case MICROSECONDS:
                return "us";
            case MILLISECONDS:
                return "ms";
            case SECONDS:
                return "s";
            case MINUTES:
                return "m";
            case HOURS:
                return "h";
            case DAYS:
                return "d";
            default:
                throw new IllegalArgumentException("Unrecognized TimeUnit: " + unit);
        }
    }

    public static void gracefullyShutdown(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(1, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(1, TimeUnit.SECONDS)) {
                    logger.info("kafka consumer pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}


