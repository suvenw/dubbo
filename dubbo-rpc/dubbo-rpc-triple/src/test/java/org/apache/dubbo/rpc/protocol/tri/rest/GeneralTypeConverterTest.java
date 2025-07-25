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
package org.apache.dubbo.rpc.protocol.tri.rest;

import org.apache.dubbo.common.utils.JsonUtils;
import org.apache.dubbo.rpc.model.FrameworkModel;
import org.apache.dubbo.rpc.protocol.tri.rest.argument.GeneralTypeConverter;
import org.apache.dubbo.rpc.protocol.tri.rest.util.TypeUtils;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GeneralTypeConverterTest {
    private static final Logger logger = LoggerFactory.getLogger(GeneralTypeConverterTest.class);

    public List<? extends Number>[] items;

    @Test
    void convert() throws NoSuchFieldException {
        GeneralTypeConverter smartConverter = new GeneralTypeConverter(FrameworkModel.defaultModel());
        smartConverter.convert(
                "23,56", GeneralTypeConverterTest.class.getField("items").getGenericType());
    }

    @Test
    void convert1() {
        Object convert = JsonUtils.toJavaObject("[1,\"aa\"]", List.class);
        Assertions.assertEquals(2, ((List) convert).size());
    }

    @Test
    void convert2() throws NoSuchFieldException {
        Class<?> type = TypeUtils.getActualType(
                GeneralTypeConverterTest.class.getField("items").getGenericType());
        logger.info(String.valueOf(type));
    }
}
