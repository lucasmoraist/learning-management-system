package com.lucasmoraist.lms.application.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class HeaderUtils {

    public static String getBearerToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

}
