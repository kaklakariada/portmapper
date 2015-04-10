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
    private static final String LOGGER_NAME = "ROOT";// "org.chris.portmapper";

    private final LoggerContext loggerContext;

    public LogbackConfiguration() {
        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    }

    public void registerOutputStream(final OutputStream logMessageOutputStream) {
        final Encoder<ILoggingEvent> encoder = createPatternLayoutEncoder(PATTERN_LAYOUT);
        final OutputStreamAppender<ILoggingEvent> appender = createAppender(logMessageOutputStream, encoder);
        configureLogger(appender);
    }

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

    public void setLogLevel(final String logLevel) {
        final Level level = Level.toLevel(logLevel);
        getLogger().setLevel(level);
    }
}
