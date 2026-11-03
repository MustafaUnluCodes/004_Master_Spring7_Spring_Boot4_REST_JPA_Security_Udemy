package com.eazybytes.jobportal.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import org.springframework.boot.logging.structured.StructuredLogFormatter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class JobPortalLogFormatter implements StructuredLogFormatter<ILoggingEvent> {

    private static final DateTimeFormatter TS =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    @Override
    public String format(ILoggingEvent event) {
        StringBuilder sb = new StringBuilder(256);
        sb.append("{\"timestamp\":\"").append(TS.format(Instant.ofEpochMilli(event.getTimeStamp()))).append('"');
        sb.append(",\"level\":\"").append(event.getLevel()).append('"');
        sb.append(",\"logger\":\"").append(escape(event.getLoggerName())).append('"');
        sb.append(",\"message\":\"").append(escape(event.getFormattedMessage())).append('"');
        if (event.getThrowableProxy() != null) {
            sb.append(",\"exception\":\"")
                    .append(escape(ThrowableProxyUtil.asString(event.getThrowableProxy())))
                    .append('"');
        }
        return sb.append("}\n").toString();
    }

    private static String escape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"'  -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
                }
            }
        }
        return sb.toString();
    }
}