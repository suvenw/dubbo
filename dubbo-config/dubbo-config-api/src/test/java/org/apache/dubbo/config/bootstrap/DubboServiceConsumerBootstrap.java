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
package org.apache.dubbo.config.bootstrap;

import org.apache.dubbo.config.MetadataReportConfig;
import org.apache.dubbo.config.bootstrap.rest.UserService;
import org.apache.dubbo.test.check.registrycenter.config.ZookeeperRegistryCenterConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dubbo Provider Bootstrap
 *
 * @since 2.7.5
 */
public class DubboServiceConsumerBootstrap {
    private static final Logger logger = LoggerFactory.getLogger(DubboServiceConsumerBootstrap.class);

    public static void main(String[] args) throws Exception {

        DubboBootstrap bootstrap = DubboBootstrap.getInstance()
                .application("dubbo-consumer-demo")
                .protocol(builder -> builder.port(20887).name("dubbo"))
                .registry(
                        "zookeeper",
                        builder -> builder.address(ZookeeperRegistryCenterConfig.getConnectionAddress()
                                + "?registry-type=service&subscribed-services=dubbo-provider-demo"))
                .metadataReport(new MetadataReportConfig(ZookeeperRegistryCenterConfig.getConnectionAddress()))
                .reference("echo", builder -> builder.interfaceClass(EchoService.class)
                        .protocol("dubbo"))
                .reference("user", builder -> builder.interfaceClass(UserService.class)
                        .protocol("tri"))
                .start();

        EchoService echoService = bootstrap.getCache().get(EchoService.class);

        for (int i = 0; i < 500; i++) {
            Thread.sleep(2000L);
            logger.info(echoService.echo("Hello,World"));
        }
    }
}
