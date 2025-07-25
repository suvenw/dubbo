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
package org.apache.dubbo.rpc.proxy;

import org.apache.dubbo.rpc.RpcContext;

import java.rmi.RemoteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteServiceImpl implements RemoteService {
    private static final Logger logger = LoggerFactory.getLogger(RemoteServiceImpl.class);

    public String getThreadName() throws RemoteException {
        logger.debug(
                "RpcContext.getServerAttachment().getRemoteHost()={}",
                RpcContext.getServiceContext().getRemoteHost());
        return Thread.currentThread().getName();
    }

    public String sayHello(String name) throws RemoteException {
        return "hello " + name + "@" + RemoteServiceImpl.class.getName();
    }

    public String sayHello(String name, String arg2) {
        return "hello " + name + "@" + RemoteServiceImpl.class.getName() + ", arg2 " + arg2;
    }
}
