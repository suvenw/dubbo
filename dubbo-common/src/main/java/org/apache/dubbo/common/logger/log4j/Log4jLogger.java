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
package org.apache.dubbo.common.logger.log4j;

import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.support.FailsafeLogger;

import org.apache.log4j.Level;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

public class Log4jLogger implements Logger {

    private final String fqcn;

    private final org.apache.log4j.Logger logger;

    public Log4jLogger(org.apache.log4j.Logger logger) {
        this.fqcn = FailsafeLogger.class.getName();
        this.logger = logger;
    }

    public Log4jLogger(String fqcn, org.apache.log4j.Logger logger) {
        this.fqcn = fqcn;
        this.logger = logger;
    }

    @Override
    public void trace(String msg) {
        logger.log(fqcn, Level.TRACE, msg, null);
    }

    @Override
    public void trace(String msg, Object... arguments) {
        FormattingTuple ft = MessageFormatter.arrayFormat(msg, arguments);
        logger.log(fqcn, Level.TRACE, ft.getMessage(), ft.getThrowable());
    }

    @Override
    public void trace(Throwable e) {
        logger.log(fqcn, Level.TRACE, e == null ? null : e.getMessage(), e);
    }

    @Override
    public void trace(String msg, Throwable e) {
        logger.log(fqcn, Level.TRACE, msg, e);
    }

    @Override
    public void debug(String msg) {
        logger.log(fqcn, Level.DEBUG, msg, null);
    }

    @Override
    public void debug(String msg, Object... arguments) {
        FormattingTuple ft = MessageFormatter.arrayFormat(msg, arguments);
        logger.log(fqcn, Level.DEBUG, ft.getMessage(), ft.getThrowable());
    }

    @Override
    public void debug(Throwable e) {
        logger.log(fqcn, Level.DEBUG, e == null ? null : e.getMessage(), e);
    }

    @Override
    public void debug(String msg, Throwable e) {
        logger.log(fqcn, Level.DEBUG, msg, e);
    }

    @Override
    public void info(String msg) {
        logger.log(fqcn, Level.INFO, msg, null);
    }

    @Override
    public void info(String msg, Object... arguments) {
        FormattingTuple ft = MessageFormatter.arrayFormat(msg, arguments);
        logger.log(fqcn, Level.INFO, ft.getMessage(), ft.getThrowable());
    }

    @Override
    public void info(Throwable e) {
        logger.log(fqcn, Level.INFO, e == null ? null : e.getMessage(), e);
    }

    @Override
    public void info(String msg, Throwable e) {
        logger.log(fqcn, Level.INFO, msg, e);
    }

    @Override
    public void warn(String msg) {
        logger.log(fqcn, Level.WARN, msg, null);
    }

    @Override
    public void warn(String msg, Object... arguments) {
        FormattingTuple ft = MessageFormatter.arrayFormat(msg, arguments);
        logger.log(fqcn, Level.WARN, ft.getMessage(), ft.getThrowable());
    }

    @Override
    public void warn(Throwable e) {
        logger.log(fqcn, Level.WARN, e == null ? null : e.getMessage(), e);
    }

    @Override
    public void warn(String msg, Throwable e) {
        logger.log(fqcn, Level.WARN, msg, e);
    }

    @Override
    public void error(String msg) {
        logger.log(fqcn, Level.ERROR, msg, null);
    }

    @Override
    public void error(String msg, Object... arguments) {
        FormattingTuple ft = MessageFormatter.arrayFormat(msg, arguments);
        logger.log(fqcn, Level.ERROR, ft.getMessage(), ft.getThrowable());
    }

    @Override
    public void error(Throwable e) {
        logger.log(fqcn, Level.ERROR, e == null ? null : e.getMessage(), e);
    }

    @Override
    public void error(String msg, Throwable e) {
        logger.log(fqcn, Level.ERROR, msg, e);
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isEnabledFor(Level.WARN);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isEnabledFor(Level.ERROR);
    }
}
