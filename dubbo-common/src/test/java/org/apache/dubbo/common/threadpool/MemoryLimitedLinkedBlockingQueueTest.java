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
package org.apache.dubbo.common.threadpool;

import java.lang.instrument.Instrumentation;

import net.bytebuddy.agent.ByteBuddyAgent;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class MemoryLimitedLinkedBlockingQueueTest {
    private static final Logger logger = LoggerFactory.getLogger(MemoryLimitedLinkedBlockingQueueTest.class);

    @Test
    void test() {
        ByteBuddyAgent.install();
        final Instrumentation instrumentation = ByteBuddyAgent.getInstrumentation();
        MemoryLimitedLinkedBlockingQueue<Runnable> queue = new MemoryLimitedLinkedBlockingQueue<>(1, instrumentation);
        // an object needs more than 1 byte of space, so it will fail here
        assertThat(queue.offer(() -> logger.info("add fail")), is(false));

        // will success
        queue.setMemoryLimit(Integer.MAX_VALUE);
        assertThat(queue.offer(() -> logger.info("add success")), is(true));
    }
}
