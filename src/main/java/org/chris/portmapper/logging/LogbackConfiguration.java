/**
 * UPnP PortMapper - A tool for managing port forwardings via UPnP
 * Copyright (C) 2015 Christoph Pirkl <christoph at users.sourceforge.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.chris.portmapper.logging;

import java.io.OutputStream;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.encoder.Encoder;

public class LogbackConfiguration {

    private static final String PATTERN_LAYOUT = "%-5level %msg%n";
    private static final String OUTPUT_STREAM_APPENDER_NAME = "OUTPUT_STREAM";
    private static final String LOGGER_NAME = "ROOT";

    private final LoggerContext loggerContext;

    public LogbackConfiguration() {
        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    }

    public void registerOutputStream(final OutputStream logMessageOutputStream) {
        final Encoder<ILoggingEvent> encoder = createPatternLayoutEncoder(PATTERN_LAYOUT);
        final OutputStreamAppender<ILoggingEvent> appender = createAppender(logMessageOutputStream, encoder);
        configureLogger(appender);
    }

    @SuppressWarnings("java:S4792") // Logger configuration is ok
    private void configureLogger(final OutputStreamAppender<ILoggingEvent> appender) {
        final Logger logbackLogger = getLogger();
        logbackLogger.addAppender(appender);
        logbackLogger.setAdditive(false);
    }

    private Logger getLogger() {
        return (Logger) LoggerFactory.getLogger(LOGGER_NAME);
    }

    private OutputStreamAppender<ILoggingEvent> createAppender(final OutputStream logMessageOutputStream,
            final Encoder<ILoggingEvent> encoder) {
        final OutputStreamAppender<ILoggingEvent> appender = new OutputStreamAppender<ILoggingEvent>();
        appender.setContext(loggerContext);
        appender.setEncoder(encoder);
        appender.setOutputStream(logMessageOutputStream);
        appender.setName(OUTPUT_STREAM_APPENDER_NAME);
        appender.start();
        return appender;
    }

    private PatternLayoutEncoder createPatternLayoutEncoder(final String pattern) {
        final PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern(pattern);
        encoder.start();
        return encoder;
    }

    @SuppressWarnings("java:S4792") // Logger configuration is ok
    public void setLogLevel(final String logLevel) {
        final Level level = Level.toLevel(logLevel);
        getLogger().setLevel(level);
    }
}
