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
package org.apache.dubbo.rpc.support;

import org.apache.dubbo.rpc.CustomArgument;
import org.apache.dubbo.rpc.RpcContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DemoServiceImpl
 */
public class DemoServiceImpl implements DemoService {
    private static final Logger logger = LoggerFactory.getLogger(DemoServiceImpl.class);

    public DemoServiceImpl() {
        super();
    }

    public void sayHello(String name) {
        logger.info("hello {}", name);
    }

    public String echo(String text) {
        return text;
    }

    public long timestamp() {
        return System.currentTimeMillis();
    }

    public String getThreadName() {
        return Thread.currentThread().getName();
    }

    public int getSize(String[] strs) {
        if (strs == null) return -1;
        return strs.length;
    }

    public int getSize(Object[] os) {
        if (os == null) return -1;
        return os.length;
    }

    public Object invoke(String service, String method) throws Exception {
        logger.info(
                "RpcContext.getServerAttachment().getRemoteHost()={}",
                RpcContext.getServiceContext().getRemoteHost());
        return service + ":" + method;
    }

    public Type enumlength(Type... types) {
        if (types.length == 0) return Type.Lower;
        return types[0];
    }

    public Type enumlength(Type type) {
        return type;
    }

    public int stringLength(String str) {
        return str.length();
    }

    public String get(CustomArgument arg1) {
        return arg1.toString();
    }

    public byte getbyte(byte arg) {
        return arg;
    }

    public Person getPerson(Person person) {
        return person;
    }

    @Override
    public String testReturnType(String str) {
        return null;
    }

    @Override
    public List<String> testReturnType1(String str) {
        return null;
    }

    @Override
    public CompletableFuture<String> testReturnType2(String str) {
        return null;
    }

    @Override
    public CompletableFuture<List<String>> testReturnType3(String str) {
        return null;
    }

    @Override
    public CompletableFuture testReturnType4(String str) {
        return CompletableFuture.completedFuture("");
    }

    @Override
    public CompletableFuture<Map<String, String>> testReturnType5(String str) {
        return null;
    }

    @Override
    public void $invoke(String s1, String s2) {}
}
