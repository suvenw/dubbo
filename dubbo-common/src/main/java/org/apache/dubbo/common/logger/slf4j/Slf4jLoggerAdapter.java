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
package org.apache.dubbo.common.logger.slf4j;

import org.apache.dubbo.common.logger.Level;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerAdapter;
import org.apache.dubbo.common.utils.ClassUtils;

import java.io.File;

import org.slf4j.LoggerFactory;

public class Slf4jLoggerAdapter implements LoggerAdapter {
    public static final String NAME = "slf4j";

    private Level level;
    private File file;

    private static final org.slf4j.Logger ROOT_LOGGER = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

    public Slf4jLoggerAdapter() {
        this.level = Slf4jLogger.getLevel(ROOT_LOGGER);
    }

    @Override
    public Logger getLogger(String key) {
        return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(key));
    }

    @Override
    public Logger getLogger(Class<?> key) {
        return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(key));
    }

    @Override
    public Logger getLogger(String fqcn, Class<?> key) {
        return new Slf4jLogger(fqcn, org.slf4j.LoggerFactory.getLogger(key));
    }

    @Override
    public Logger getLogger(String fqcn, String key) {
        return new Slf4jLogger(fqcn, org.slf4j.LoggerFactory.getLogger(key));
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public void setLevel(Level level) {
        System.err.printf(
                "The level of slf4j logger current can not be set, using the default level: %s \n",
                Slf4jLogger.getLevel(ROOT_LOGGER));
        this.level = level;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public boolean isConfigured() {
        try {
            ClassUtils.forName("org.slf4j.impl.StaticLoggerBinder");
            return true;
        } catch (ClassNotFoundException ignore) {
            // ignore
        }
        return false;
    }
}
