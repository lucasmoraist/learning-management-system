package com.lucasmoraist.lms.application.utils;

import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class TraceIdUtils {

    public static String generateTraceId() {
        String hex = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        return String.format("%s-%s-%s-%s",
                hex.substring(0, 8),
                hex.substring(8, 16),
                hex.substring(16, 24),
                hex.substring(24, 32));
    }

}
