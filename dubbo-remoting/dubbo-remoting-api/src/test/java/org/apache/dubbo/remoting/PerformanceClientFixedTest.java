/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.remoting;

import org.apache.dubbo.common.logger.ErrorTypeAwareLogger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.serialize.support.DefaultSerializationSelector;
import org.apache.dubbo.remoting.exchange.ExchangeClient;
import org.apache.dubbo.remoting.exchange.Exchangers;

import java.util.ArrayList;
import java.util.Random;

import org.junit.jupiter.api.Test;

import static org.apache.dubbo.common.constants.CommonConstants.DEFAULT_TIMEOUT;
import static org.apache.dubbo.common.constants.CommonConstants.TIMEOUT_KEY;
import static org.apache.dubbo.common.constants.LoggerCodeConstants.CONFIG_UNDEFINED_ARGUMENT;
import static org.apache.dubbo.remoting.Constants.CONNECTIONS_KEY;

class PerformanceClientFixedTest {

    private static final ErrorTypeAwareLogger logger =
            LoggerFactory.getErrorTypeAwareLogger(PerformanceClientTest.class);

    @Test
    void testClient() throws Exception {
        // read the parameters
        if (PerformanceUtils.getProperty("server", null) == null) {
            logger.warn(CONFIG_UNDEFINED_ARGUMENT, "", "", "Please set -Dserver=127.0.0.1:9911");
            return;
        }
        final String server = System.getProperty("server", "127.0.0.1:9911");
        final String transporter =
                PerformanceUtils.getProperty(Constants.TRANSPORTER_KEY, Constants.DEFAULT_TRANSPORTER);
        final String serialization = PerformanceUtils.getProperty(
                Constants.SERIALIZATION_KEY, DefaultSerializationSelector.getDefaultRemotingSerialization());
        final int timeout = PerformanceUtils.getIntProperty(TIMEOUT_KEY, DEFAULT_TIMEOUT);
        // final int length = PerformanceUtils.getIntProperty("length", 1024);
        final int connectionCount = PerformanceUtils.getIntProperty(CONNECTIONS_KEY, 1);
        // final int concurrent = PerformanceUtils.getIntProperty("concurrent", 100);
        // int r = PerformanceUtils.getIntProperty("runs", 10000);
        // final int runs = r > 0 ? r : Integer.MAX_VALUE;
        // final String onerror = PerformanceUtils.getProperty("onerror", "continue");
        final String url = "exchange://" + server + "?transporter=" + transporter + "&serialization=" + serialization
                + "&timeout=" + timeout;

        // int idx = server.indexOf(':');
        Random rd = new Random(connectionCount);
        ArrayList<ExchangeClient> arrays = new ArrayList<ExchangeClient>();
        String oneKBlock = null;
        String messageBlock = null;
        int s = 0;
        int f = 0;
        logger.info("initialize arrays " + url);
        while (s < connectionCount) {
            ExchangeClient client = null;
            try {
                logger.info("open connection " + s + " " + url + arrays.size());

                client = Exchangers.connect(url);

                logger.info("run after open");

                if (client.isConnected()) {
                    arrays.add(client);
                    s++;
                    logger.info("open client success " + s);
                } else {
                    logger.info("open client failed, try again.");
                }
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                if (client != null && !client.isConnected()) {
                    f++;
                    logger.info("open client failed, try again " + f);
                    client.close();
                }
            }
        }

        StringBuilder sb1 = new StringBuilder();
        Random rd2 = new Random();
        char[] numbersAndLetters =
                ("0123456789abcdefghijklmnopqrstuvwxyz" + "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
        int size1 = numbersAndLetters.length;
        for (int j = 0; j < 1024; j++) {
            sb1.append(numbersAndLetters[rd2.nextInt(size1)]);
        }
        oneKBlock = sb1.toString();

        for (int j = 0; j < Integer.MAX_VALUE; j++) {
            try {
                String size = "10";

                int request_size = 10;
                try {
                    request_size = Integer.parseInt(size);
                } catch (Throwable t) {
                    request_size = 10;
                }

                if (messageBlock == null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < request_size; i++) {
                        sb.append(oneKBlock);
                    }
                    messageBlock = sb.toString();

                    logger.info("set messageBlock to " + messageBlock);
                }
                int index = rd.nextInt(connectionCount);
                ExchangeClient client = arrays.get(index);
                // ExchangeClient client = arrays.get(0);
                String output = (String) client.request(messageBlock).get();

                if (output.lastIndexOf(messageBlock) < 0) {
                    logger.info("send messageBlock;get " + output);
                    throw new Throwable("return results invalid");
                } else {
                    if (j % 100 == 0) logger.info("OK: " + j);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}
