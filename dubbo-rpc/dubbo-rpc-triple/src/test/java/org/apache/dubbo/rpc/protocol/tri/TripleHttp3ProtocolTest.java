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
package org.apache.dubbo.rpc.protocol.tri;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.stream.StreamObserver;
import org.apache.dubbo.common.utils.ClassUtils;
import org.apache.dubbo.common.utils.NetUtils;
import org.apache.dubbo.config.SslConfig;
import org.apache.dubbo.rpc.Constants;
import org.apache.dubbo.rpc.Exporter;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Protocol;
import org.apache.dubbo.rpc.ProxyFactory;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.apache.dubbo.rpc.model.ConsumerModel;
import org.apache.dubbo.rpc.model.ModuleServiceRepository;
import org.apache.dubbo.rpc.model.ProviderModel;
import org.apache.dubbo.rpc.model.ServiceDescriptor;
import org.apache.dubbo.rpc.model.ServiceMetadata;
import org.apache.dubbo.rpc.protocol.tri.support.IGreeter;
import org.apache.dubbo.rpc.protocol.tri.support.IGreeterImpl;
import org.apache.dubbo.rpc.protocol.tri.support.MockStreamObserver;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TripleHttp3ProtocolTest {

    @Test
    void testDemoProtocol() throws Exception {
        IGreeterImpl serviceImpl = new IGreeterImpl();

        int availablePort = NetUtils.getAvailablePort();
        ApplicationModel applicationModel = ApplicationModel.defaultModel();

        Map<String, String> settings = new HashMap<>();
        settings.put(Constants.H3_SETTINGS_HTTP3_ENABLED, "true");
        settings.put(Constants.H3_SETTINGS_HTTP3_NEGOTIATION, "false");
        applicationModel.modelEnvironment().updateAppConfigMap(settings);

        SslConfig sslConfig = new SslConfig();
        sslConfig.setScopeModel(applicationModel);
        sslConfig.setServerKeyCertChainPath(getAbsolutePath("/certs/server.pem"));
        sslConfig.setServerPrivateKeyPath(getAbsolutePath("/certs/server.key"));
        sslConfig.setServerTrustCertCollectionPath(getAbsolutePath("/certs/ca.pem"));
        sslConfig.setClientKeyCertChainPath(getAbsolutePath("/certs/client.pem"));
        sslConfig.setClientPrivateKeyPath(getAbsolutePath("/certs/client.key"));
        sslConfig.setClientTrustCertCollectionPath(getAbsolutePath("/certs/ca.pem"));
        applicationModel.getApplicationConfigManager().setSsl(sslConfig);

        URL providerUrl = URL.valueOf("tri://127.0.0.1:" + availablePort + "/" + IGreeter.class.getName());

        ModuleServiceRepository serviceRepository =
                applicationModel.getDefaultModule().getServiceRepository();
        ServiceDescriptor serviceDescriptor = serviceRepository.registerService(IGreeter.class);

        ProviderModel providerModel = new ProviderModel(
                providerUrl.getServiceKey(),
                serviceImpl,
                serviceDescriptor,
                new ServiceMetadata(),
                ClassUtils.getClassLoader(IGreeter.class));
        serviceRepository.registerProvider(providerModel);
        providerUrl = providerUrl.setServiceModel(providerModel);

        Protocol protocol = new TripleProtocol(providerUrl.getOrDefaultFrameworkModel());
        ProxyFactory proxy =
                applicationModel.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension();
        Invoker<IGreeter> invoker = proxy.getInvoker(serviceImpl, IGreeter.class, providerUrl);
        Exporter<IGreeter> export = protocol.export(invoker);

        URL consumerUrl = URL.valueOf("tri://127.0.0.1:" + availablePort + "/" + IGreeter.class.getName());

        ConsumerModel consumerModel =
                new ConsumerModel(consumerUrl.getServiceKey(), null, serviceDescriptor, null, null, null);
        consumerUrl = consumerUrl.setServiceModel(consumerModel);
        IGreeter greeterProxy = proxy.getProxy(protocol.refer(IGreeter.class, consumerUrl));
        Thread.sleep(1000);

        Assertions.assertTrue(Http3Exchanger.isEnabled(providerUrl));

        // 1. test unaryStream
        String REQUEST_MSG = "hello world";
        Assertions.assertEquals(REQUEST_MSG, greeterProxy.echo(REQUEST_MSG));
        Assertions.assertEquals(REQUEST_MSG, serviceImpl.echoAsync(REQUEST_MSG).get());

        // 2. test serverStream
        MockStreamObserver outboundMessageSubscriber1 = new MockStreamObserver();
        greeterProxy.serverStream(REQUEST_MSG, outboundMessageSubscriber1);
        outboundMessageSubscriber1.getLatch().await(3000, TimeUnit.MILLISECONDS);
        Assertions.assertEquals(REQUEST_MSG, outboundMessageSubscriber1.getOnNextData());
        Assertions.assertTrue(outboundMessageSubscriber1.isOnCompleted());

        // 3. test bidirectionalStream
        MockStreamObserver outboundMessageSubscriber2 = new MockStreamObserver();
        StreamObserver<String> inboundMessageObserver = greeterProxy.bidirectionalStream(outboundMessageSubscriber2);
        inboundMessageObserver.onNext(REQUEST_MSG);
        inboundMessageObserver.onCompleted();
        outboundMessageSubscriber2.getLatch().await(3000, TimeUnit.MILLISECONDS);
        // verify client
        Assertions.assertEquals(IGreeter.SERVER_MSG, outboundMessageSubscriber2.getOnNextData());
        Assertions.assertTrue(outboundMessageSubscriber2.isOnCompleted());
        // verify server
        MockStreamObserver serverOutboundMessageSubscriber = (MockStreamObserver) serviceImpl.getMockStreamObserver();
        serverOutboundMessageSubscriber.getLatch().await(1000, TimeUnit.MILLISECONDS);
        Assertions.assertEquals(REQUEST_MSG, serverOutboundMessageSubscriber.getOnNextData());
        Assertions.assertTrue(serverOutboundMessageSubscriber.isOnCompleted());

        export.unexport();
        protocol.destroy();
        // resource recycle.
        serviceRepository.destroy();
    }

    private static String getAbsolutePath(String resourcePath) throws Exception {
        java.net.URL resourceUrl = TripleHttp3ProtocolTest.class.getResource(resourcePath);
        Assertions.assertNotNull(resourceUrl, "Cert file '" + resourcePath + "' is required");
        return Paths.get(resourceUrl.toURI()).toAbsolutePath().toString();
    }
}
