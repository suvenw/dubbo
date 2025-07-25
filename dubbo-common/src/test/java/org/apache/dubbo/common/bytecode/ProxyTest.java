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
package org.apache.dubbo.common.bytecode;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisabledForJreRange(min = JRE.JAVA_16)
class ProxyTest {

    @Test
    void testMain() throws Exception {
        Proxy proxy = Proxy.getProxy(ITest.class, ITest.class);
        ITest instance = (ITest) proxy.newInstance((proxy1, method, args) -> {
            if ("getName".equals(method.getName())) {
                assertEquals(args.length, 0);
            } else if ("setName".equals(method.getName())) {
                assertEquals(args.length, 2);
                assertEquals(args[0], "qianlei");
                assertEquals(args[1], "hello");
            }
            return null;
        });

        assertNull(instance.getName());
        instance.setName("qianlei", "hello");
    }

    @Test
    void testCglibProxy() throws Exception {
        ITest test = (ITest) Proxy.getProxy(ITest.class).newInstance((proxy, method, args) -> {
            return null;
        });

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(test.getClass());
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> null);
        try {
            enhancer.create();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    public interface ITest {
        String getName();

        void setName(String name, String name2);

        static String sayBye() {
            return "Bye!";
        }
    }
}
