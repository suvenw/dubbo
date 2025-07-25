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

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MixinTest {
    private static final Logger logger = LoggerFactory.getLogger(MixinTest.class);

    @Test
    void testMain() {
        Mixin mixin = Mixin.mixin(new Class[] {I1.class, I2.class, I3.class}, new Class[] {C1.class, C2.class});
        Object o = mixin.newInstance(new Object[] {new C1(), new C2()});
        assertTrue(o instanceof I1);
        assertTrue(o instanceof I2);
        assertTrue(o instanceof I3);
        ((I1) o).m1();
        ((I2) o).m2();
        ((I3) o).m3();
    }

    public interface I1 {
        void m1();
    }

    public interface I2 {
        void m2();
    }

    public interface I3 {
        void m3();
    }

    public class C1 implements Mixin.MixinAware {
        public void m1() {
            logger.info("c1.m1();");
        }

        public void m2() {
            logger.info("c1.m2();");
        }

        public void setMixinInstance(Object mi) {
            logger.info("c1.setMixinInstance:{}", mi);
        }
    }

    public class C2 implements Mixin.MixinAware {
        public void m3() {
            logger.info("c2.m3();");
        }

        public void setMixinInstance(Object mi) {
            logger.info("c2.setMixinInstance:{}", mi);
        }
    }
}
