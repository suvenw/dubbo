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
package org.apache.dubbo.qos.legacy.service;

import org.apache.dubbo.rpc.RpcContext;

import java.util.Map;
import java.util.Set;

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

    public Map echo(Map map) {
        return map;
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

    public Type getType(Type type) {
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

    public Person gerPerson(Person person) {
        return person;
    }

    public Set<String> keys(Map<String, String> map) {
        return map == null ? null : map.keySet();
    }

    public void nonSerializedParameter(NonSerialized ns) {}

    public NonSerialized returnNonSerialized() {
        return new NonSerialized();
    }

    public long add(int a, long b) {
        return a + b;
    }

    @Override
    public int getPerson(Person person) {
        return person.getAge();
    }

    @Override
    public int getPerson(Person person1, Person person2) {
        return person1.getAge() + person2.getAge();
    }

    @Override
    public String getPerson(Man man) {
        return man.getName();
    }

    @Override
    public String getRemoteApplicationName() {
        return RpcContext.getServiceContext().getRemoteApplicationName();
    }

    @Override
    public Map<Integer, Object> getMap(Map<Integer, Object> map) {
        return map;
    }
}
