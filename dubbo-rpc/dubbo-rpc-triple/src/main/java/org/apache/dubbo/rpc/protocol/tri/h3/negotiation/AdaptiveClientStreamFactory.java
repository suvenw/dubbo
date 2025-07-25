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
package org.apache.dubbo.rpc.protocol.tri.h3.negotiation;

import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.remoting.api.connection.AbstractConnectionClient;
import org.apache.dubbo.rpc.model.FrameworkModel;
import org.apache.dubbo.rpc.protocol.tri.call.TripleClientCall;
import org.apache.dubbo.rpc.protocol.tri.h12.http2.Http2TripleClientStream;
import org.apache.dubbo.rpc.protocol.tri.h3.Http3TripleClientStream;
import org.apache.dubbo.rpc.protocol.tri.stream.ClientStream;
import org.apache.dubbo.rpc.protocol.tri.stream.ClientStreamFactory;
import org.apache.dubbo.rpc.protocol.tri.transport.TripleWriteQueue;

import java.util.concurrent.Executor;

import io.netty.channel.Channel;

@Activate(order = -90, onClass = "io.netty.handler.codec.quic.QuicChannel")
public class AdaptiveClientStreamFactory implements ClientStreamFactory {

    @Override
    public ClientStream createClientStream(
            AbstractConnectionClient client,
            FrameworkModel frameworkModel,
            Executor executor,
            TripleClientCall clientCall,
            TripleWriteQueue writeQueue) {
        if (client instanceof AutoSwitchConnectionClient) {
            Channel channel = client.getChannel(true);
            if (((AutoSwitchConnectionClient) client).isHttp3Connected()) {
                return new Http3TripleClientStream(frameworkModel, executor, channel, clientCall, writeQueue);
            }
            return new Http2TripleClientStream(frameworkModel, executor, channel, clientCall, writeQueue);
        }
        return null;
    }
}
