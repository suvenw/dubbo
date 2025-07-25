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
package org.apache.dubbo.rpc.cluster.support;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.utils.NetUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.metrics.event.MetricsDispatcher;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.RpcInvocation;
import org.apache.dubbo.rpc.cluster.Directory;
import org.apache.dubbo.rpc.cluster.LoadBalance;
import org.apache.dubbo.rpc.cluster.directory.StaticDirectory;
import org.apache.dubbo.rpc.cluster.filter.DemoService;
import org.apache.dubbo.rpc.cluster.loadbalance.LeastActiveLoadBalance;
import org.apache.dubbo.rpc.cluster.loadbalance.RandomLoadBalance;
import org.apache.dubbo.rpc.cluster.loadbalance.RoundRobinLoadBalance;
import org.apache.dubbo.rpc.model.ApplicationModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.apache.dubbo.common.constants.CommonConstants.CONSUMER;
import static org.apache.dubbo.common.constants.CommonConstants.ENABLE_CONNECTIVITY_VALIDATION;
import static org.apache.dubbo.common.constants.CommonConstants.INTERFACE_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.MONITOR_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.PATH_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.PROTOCOL_KEY;
import static org.apache.dubbo.rpc.cluster.Constants.CLUSTER_AVAILABLE_CHECK_KEY;
import static org.apache.dubbo.rpc.cluster.Constants.REFER_KEY;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * AbstractClusterInvokerTest
 */
@SuppressWarnings("rawtypes")
class AbstractClusterInvokerTest {
    List<Invoker<IHelloService>> invokers = new ArrayList<Invoker<IHelloService>>();
    List<Invoker<IHelloService>> selectedInvokers = new ArrayList<Invoker<IHelloService>>();
    AbstractClusterInvoker<IHelloService> cluster;
    AbstractClusterInvoker<IHelloService> cluster_nocheck;
    StaticDirectory<IHelloService> dic;
    RpcInvocation invocation = new RpcInvocation();
    URL url = URL.valueOf(
            "registry://localhost:9090/org.apache.dubbo.rpc.cluster.support.AbstractClusterInvokerTest.IHelloService?refer="
                    + URL.encode("application=abstractClusterInvokerTest"));
    URL consumerUrl = URL.valueOf(
            "dubbo://localhost?application=abstractClusterInvokerTest&refer=application%3DabstractClusterInvokerTest");

    Invoker<IHelloService> invoker1;
    Invoker<IHelloService> invoker2;
    Invoker<IHelloService> invoker3;
    Invoker<IHelloService> invoker4;
    Invoker<IHelloService> invoker5;
    Invoker<IHelloService> mockedInvoker1;

    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
        System.setProperty(ENABLE_CONNECTIVITY_VALIDATION, "false");
    }

    @AfterEach
    public void teardown() throws Exception {
        RpcContext.removeContext();
    }

    @AfterAll
    public static void afterClass() {
        System.clearProperty(ENABLE_CONNECTIVITY_VALIDATION);
    }

    @SuppressWarnings({"unchecked"})
    @BeforeEach
    public void setUp() throws Exception {
        ApplicationModel.defaultModel().getBeanFactory().registerBean(MetricsDispatcher.class);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("application", "abstractClusterInvokerTest");
        url = url.putAttribute(REFER_KEY, attributes);

        invocation.setMethodName("sayHello");

        invoker1 = mock(Invoker.class);
        invoker2 = mock(Invoker.class);
        invoker3 = mock(Invoker.class);
        invoker4 = mock(Invoker.class);
        invoker5 = mock(Invoker.class);
        mockedInvoker1 = mock(Invoker.class);

        URL turl = URL.valueOf("test://test:11/test");

        given(invoker1.isAvailable()).willReturn(false);
        given(invoker1.getInterface()).willReturn(IHelloService.class);
        given(invoker1.getUrl()).willReturn(turl.setPort(1).addParameter("name", "invoker1"));

        given(invoker2.isAvailable()).willReturn(true);
        given(invoker2.getInterface()).willReturn(IHelloService.class);
        given(invoker2.getUrl()).willReturn(turl.setPort(2).addParameter("name", "invoker2"));

        given(invoker3.isAvailable()).willReturn(false);
        given(invoker3.getInterface()).willReturn(IHelloService.class);
        given(invoker3.getUrl()).willReturn(turl.setPort(3).addParameter("name", "invoker3"));

        given(invoker4.isAvailable()).willReturn(true);
        given(invoker4.getInterface()).willReturn(IHelloService.class);
        given(invoker4.getUrl()).willReturn(turl.setPort(4).addParameter("name", "invoker4"));

        given(invoker5.isAvailable()).willReturn(false);
        given(invoker5.getInterface()).willReturn(IHelloService.class);
        given(invoker5.getUrl()).willReturn(turl.setPort(5).addParameter("name", "invoker5"));

        given(mockedInvoker1.isAvailable()).willReturn(false);
        given(mockedInvoker1.getInterface()).willReturn(IHelloService.class);
        given(mockedInvoker1.getUrl()).willReturn(turl.setPort(999).setProtocol("mock"));

        invokers.add(invoker1);
        dic = new StaticDirectory<IHelloService>(url, invokers, null);
        cluster = new AbstractClusterInvoker(dic) {
            @Override
            protected Result doInvoke(Invocation invocation, List invokers, LoadBalance loadbalance)
                    throws RpcException {
                return null;
            }
        };

        cluster_nocheck =
                new AbstractClusterInvoker(
                        dic, url.addParameterIfAbsent(CLUSTER_AVAILABLE_CHECK_KEY, Boolean.FALSE.toString())) {
                    @Override
                    protected Result doInvoke(Invocation invocation, List invokers, LoadBalance loadbalance)
                            throws RpcException {
                        return null;
                    }
                };
    }

    @Disabled(
            "RpcContext attachments will be set to Invocation twice, first in ConsumerContextFilter, second AbstractInvoker")
    @Test
    void testBindingAttachment() {
        final String attachKey = "attach";
        final String attachValue = "value";

        // setup attachment
        RpcContext.getClientAttachment().setAttachment(attachKey, attachValue);
        Map<String, Object> attachments = RpcContext.getClientAttachment().getObjectAttachments();
        Assertions.assertTrue(attachments != null && attachments.size() == 1, "set attachment failed!");

        cluster = new AbstractClusterInvoker(dic) {
            @Override
            protected Result doInvoke(Invocation invocation, List invokers, LoadBalance loadbalance)
                    throws RpcException {
                // attachment will be bind to invocation
                String value = invocation.getAttachment(attachKey);
                Assertions.assertNotNull(value);
                Assertions.assertEquals(attachValue, value, "binding attachment failed!");
                return null;
            }
        };

        // invoke
        cluster.invoke(invocation);
    }

    @Test
    void testSelect_Invokersize0() {
        LoadBalance l = cluster.initLoadBalance(invokers, invocation);
        Assertions.assertNotNull(l, "cluster.initLoadBalance returns null!");
        {
            Invoker invoker = cluster.select(l, null, null, null);
            Assertions.assertNull(invoker);
        }
        {
            invokers.clear();
            selectedInvokers.clear();
            Invoker invoker = cluster.select(l, null, invokers, null);
            Assertions.assertNull(invoker);
        }
    }

    @Test
    void testSelectedInvokers() {
        cluster = new AbstractClusterInvoker(dic) {
            @Override
            protected Result doInvoke(Invocation invocation, List invokers, LoadBalance loadbalance)
                    throws RpcException {
                checkInvokers(invokers, invocation);
                Invoker invoker = select(loadbalance, invocation, invokers, null);
                return invokeWithContext(invoker, invocation);
            }
        };

        // invoke
        cluster.invoke(invocation);

        Assertions.assertEquals(Collections.singletonList(invoker1), invocation.getInvokedInvokers());
    }

    @Test
    void testSelect_Invokersize1() {
        invokers.clear();
        invokers.add(invoker1);
        LoadBalance l = cluster.initLoadBalance(invokers, invocation);
        Assertions.assertNotNull(l, "cluster.initLoadBalance returns null!");
        Invoker invoker = cluster.select(l, null, invokers, null);
        Assertions.assertEquals(invoker1, invoker);
    }

    @Test
    void testSelect_Invokersize2AndselectNotNull() {
        invokers.clear();
        invokers.add(invoker2);
        invokers.add(invoker4);
        LoadBalance l = cluster.initLoadBalance(invokers, invocation);
        Assertions.assertNotNull(l, "cluster.initLoadBalance returns null!");
        {
            selectedInvokers.clear();
            selectedInvokers.add(invoker4);
            Invoker invoker = cluster.select(l, invocation, invokers, selectedInvokers);
            Assertions.assertEquals(invoker2, invoker);
        }
        {
            selectedInvokers.clear();
            selectedInvokers.add(invoker2);
            Invoker invoker = cluster.select(l, invocation, invokers, selectedInvokers);
            Assertions.assertEquals(invoker4, invoker);
        }
    }

    @Test
    void testSelect_multiInvokers() {
        testSelect_multiInvokers(RoundRobinLoadBalance.NAME);
        testSelect_multiInvokers(LeastActiveLoadBalance.NAME);
        testSelect_multiInvokers(RandomLoadBalance.NAME);
    }

    @Test
    void testCloseAvailablecheck() {
        LoadBalance lb = mock(LoadBalance.class);
        Map<String, String> queryMap = (Map<String, String>) url.getAttribute(REFER_KEY);
        URL tmpUrl = turnRegistryUrlToConsumerUrl(url, queryMap);

        when(lb.select(same(invokers), eq(tmpUrl), same(invocation))).thenReturn(invoker1);
        initlistsize5();

        Invoker sinvoker = cluster_nocheck.select(lb, invocation, invokers, selectedInvokers);
        Assertions.assertFalse(sinvoker.isAvailable());
        Assertions.assertEquals(invoker1, sinvoker);
    }

    private URL turnRegistryUrlToConsumerUrl(URL url, Map<String, String> queryMap) {
        String host =
                StringUtils.isNotEmpty(queryMap.get("register.ip")) ? queryMap.get("register.ip") : this.url.getHost();
        String path = queryMap.get(PATH_KEY);
        String consumedProtocol = queryMap.get(PROTOCOL_KEY) == null ? CONSUMER : queryMap.get(PROTOCOL_KEY);

        URL consumerUrlFrom = this.url
                .setHost(host)
                .setPort(0)
                .setProtocol(consumedProtocol)
                .setPath(path == null ? queryMap.get(INTERFACE_KEY) : path);
        return consumerUrlFrom.addParameters(queryMap).removeParameter(MONITOR_KEY);
    }

    @Test
    void testDonotSelectAgainAndNoCheckAvailable() {

        LoadBalance lb = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(RoundRobinLoadBalance.NAME);
        initlistsize5();
        {
            // Boundary condition test .
            selectedInvokers.clear();
            selectedInvokers.add(invoker2);
            selectedInvokers.add(invoker3);
            selectedInvokers.add(invoker4);
            selectedInvokers.add(invoker5);
            Invoker sinvoker = cluster_nocheck.select(lb, invocation, invokers, selectedInvokers);
            Assertions.assertSame(invoker1, sinvoker);
        }
        {
            // Boundary condition test .
            selectedInvokers.clear();
            selectedInvokers.add(invoker1);
            selectedInvokers.add(invoker3);
            selectedInvokers.add(invoker4);
            selectedInvokers.add(invoker5);
            Invoker sinvoker = cluster_nocheck.select(lb, invocation, invokers, selectedInvokers);
            Assertions.assertSame(invoker2, sinvoker);
        }
        {
            // Boundary condition test .
            selectedInvokers.clear();
            selectedInvokers.add(invoker1);
            selectedInvokers.add(invoker2);
            selectedInvokers.add(invoker4);
            selectedInvokers.add(invoker5);
            Invoker sinvoker = cluster_nocheck.select(lb, invocation, invokers, selectedInvokers);
            Assertions.assertSame(invoker3, sinvoker);
        }
        {
            // Boundary condition test .
            selectedInvokers.clear();
            selectedInvokers.add(invoker1);
            selectedInvokers.add(invoker2);
            selectedInvokers.add(invoker3);
            selectedInvokers.add(invoker4);
            Invoker sinvoker = cluster_nocheck.select(lb, invocation, invokers, selectedInvokers);
            Assertions.assertSame(invoker5, sinvoker);
        }
        {
            // Boundary condition test .
            selectedInvokers.clear();
            selectedInvokers.add(invoker1);
            selectedInvokers.add(invoker2);
            selectedInvokers.add(invoker3);
            selectedInvokers.add(invoker4);
            selectedInvokers.add(invoker5);
            Invoker sinvoker = cluster_nocheck.select(lb, invocation, invokers, selectedInvokers);
            Assertions.assertTrue(invokers.contains(sinvoker));
        }
    }

    @Test
    void testSelectAgainAndCheckAvailable() {

        LoadBalance lb = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(RoundRobinLoadBalance.NAME);
        initlistsize5();
        {
            // Boundary condition test .
            selectedInvokers.clear();
            selectedInvokers.add(invoker1);
            selectedInvokers.add(invoker2);
            selectedInvokers.add(invoker3);
            selectedInvokers.add(invoker5);
            Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
            Assertions.assertSame(sinvoker, invoker4);
        }
        {
            // Boundary condition test .
            selectedInvokers.clear();
            selectedInvokers.add(invoker2);
            selectedInvokers.add(invoker3);
            selectedInvokers.add(invoker4);
            selectedInvokers.add(invoker5);
            Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
            Assertions.assertTrue(sinvoker == invoker2 || sinvoker == invoker4);
        }
        {
            // Boundary condition test .
            for (int i = 0; i < 100; i++) {
                selectedInvokers.clear();
                Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
                Assertions.assertTrue(sinvoker == invoker2 || sinvoker == invoker4);
            }
        }
        {
            // Boundary condition test .
            for (int i = 0; i < 100; i++) {
                selectedInvokers.clear();
                selectedInvokers.add(invoker1);
                selectedInvokers.add(invoker3);
                selectedInvokers.add(invoker5);
                Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
                Assertions.assertTrue(sinvoker == invoker2 || sinvoker == invoker4);
            }
        }
        {
            // Boundary condition test .
            for (int i = 0; i < 100; i++) {
                selectedInvokers.clear();
                selectedInvokers.add(invoker1);
                selectedInvokers.add(invoker3);
                selectedInvokers.add(invoker2);
                selectedInvokers.add(invoker4);
                selectedInvokers.add(invoker5);
                Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
                Assertions.assertTrue(sinvoker == invoker2 || sinvoker == invoker4);
            }
        }
    }

    public void testSelect_multiInvokers(String lbname) {

        int min = 100, max = 500;
        Double d = (Math.random() * (max - min + 1) + min);
        int runs = d.intValue();
        Assertions.assertTrue(runs >= min);
        LoadBalance lb = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(lbname);
        initlistsize5();
        for (int i = 0; i < runs; i++) {
            Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
            Assertions.assertTrue(sinvoker.isAvailable());

            Mockito.clearInvocations(invoker1, invoker2, invoker3, invoker4, invoker5);
        }
        for (int i = 0; i < runs; i++) {
            selectedInvokers.clear();
            selectedInvokers.add(invoker1);
            Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
            Assertions.assertTrue(sinvoker.isAvailable());

            Mockito.clearInvocations(invoker1, invoker2, invoker3, invoker4, invoker5);
        }
        for (int i = 0; i < runs; i++) {
            selectedInvokers.clear();
            selectedInvokers.add(invoker2);
            Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
            Assertions.assertTrue(sinvoker.isAvailable());

            Mockito.clearInvocations(invoker1, invoker2, invoker3, invoker4, invoker5);
        }
        for (int i = 0; i < runs; i++) {
            selectedInvokers.clear();
            selectedInvokers.add(invoker2);
            selectedInvokers.add(invoker4);
            Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
            Assertions.assertTrue(sinvoker.isAvailable());

            Mockito.clearInvocations(invoker1, invoker2, invoker3, invoker4, invoker5);
        }
        for (int i = 0; i < runs; i++) {
            selectedInvokers.clear();
            selectedInvokers.add(invoker1);
            selectedInvokers.add(invoker3);
            selectedInvokers.add(invoker5);
            Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
            Assertions.assertTrue(sinvoker.isAvailable());

            Mockito.clearInvocations(invoker1, invoker2, invoker3, invoker4, invoker5);
        }
        for (int i = 0; i < runs; i++) {

            selectedInvokers.clear();
            selectedInvokers.add(invoker1);
            selectedInvokers.add(invoker2);
            selectedInvokers.add(invoker3);
            Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
            Assertions.assertTrue(sinvoker.isAvailable());

            Mockito.clearInvocations(invoker1, invoker2, invoker3, invoker4, invoker5);
        }
    }

    /**
     * Test balance.
     */
    @Test
    void testSelectBalance() {

        LoadBalance lb = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(RoundRobinLoadBalance.NAME);
        initlistsize5();

        Map<Invoker, AtomicLong> counter = new ConcurrentHashMap<Invoker, AtomicLong>();
        for (Invoker invoker : invokers) {
            counter.put(invoker, new AtomicLong(0));
        }
        int runs = 1000;
        for (int i = 0; i < runs; i++) {
            selectedInvokers.clear();
            Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
            counter.get(sinvoker).incrementAndGet();
        }

        for (Map.Entry<Invoker, AtomicLong> entry : counter.entrySet()) {
            Long count = entry.getValue().get();
            if (entry.getKey().isAvailable())
                Assertions.assertTrue(count > runs / invokers.size(), "count should > avg");
        }

        Assertions.assertEquals(
                runs, counter.get(invoker2).get() + counter.get(invoker4).get());
    }

    private void initlistsize5() {
        invokers.clear();
        selectedInvokers
                .clear(); // Clear first, previous test case will make sure that the right invoker2 will be used.
        invokers.add(invoker1);
        invokers.add(invoker2);
        invokers.add(invoker3);
        invokers.add(invoker4);
        invokers.add(invoker5);
    }

    private void initDic() {
        dic.notify(invokers);
        dic.buildRouterChain();
    }

    @Test
    void testTimeoutExceptionCode() {
        List<Invoker<DemoService>> invokers = new ArrayList<Invoker<DemoService>>();
        invokers.add(new Invoker<DemoService>() {

            @Override
            public Class<DemoService> getInterface() {
                return DemoService.class;
            }

            public URL getUrl() {
                return URL.valueOf("dubbo://" + NetUtils.getLocalHost() + ":20880/" + DemoService.class.getName());
            }

            @Override
            public boolean isAvailable() {
                return false;
            }

            @Override
            public Result invoke(Invocation invocation) throws RpcException {
                throw new RpcException(RpcException.TIMEOUT_EXCEPTION, "test timeout");
            }

            @Override
            public void destroy() {}
        });
        Directory<DemoService> directory = new StaticDirectory<DemoService>(invokers);
        FailoverClusterInvoker<DemoService> failoverClusterInvoker = new FailoverClusterInvoker<DemoService>(directory);
        RpcInvocation invocation =
                new RpcInvocation("sayHello", DemoService.class.getName(), "", new Class<?>[0], new Object[0]);
        try {
            failoverClusterInvoker.invoke(invocation);
            Assertions.fail();
        } catch (RpcException e) {
            Assertions.assertEquals(RpcException.TIMEOUT_EXCEPTION, e.getCode());
        }
        ForkingClusterInvoker<DemoService> forkingClusterInvoker = new ForkingClusterInvoker<DemoService>(directory);
        invocation = new RpcInvocation("sayHello", DemoService.class.getName(), "", new Class<?>[0], new Object[0]);
        try {
            forkingClusterInvoker.invoke(invocation);
            Assertions.fail();
        } catch (RpcException e) {
            Assertions.assertEquals(RpcException.TIMEOUT_EXCEPTION, e.getCode());
        }
        FailfastClusterInvoker<DemoService> failfastClusterInvoker = new FailfastClusterInvoker<DemoService>(directory);
        invocation = new RpcInvocation("sayHello", DemoService.class.getName(), "", new Class<?>[0], new Object[0]);
        try {
            failfastClusterInvoker.invoke(invocation);
            Assertions.fail();
        } catch (RpcException e) {
            Assertions.assertEquals(RpcException.TIMEOUT_EXCEPTION, e.getCode());
        }
    }

    public static interface IHelloService {}
}
