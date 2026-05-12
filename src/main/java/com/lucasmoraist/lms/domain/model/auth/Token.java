package com.lucasmoraist.lms.domain.model.auth;

import lombok.Getter;

@Getter
public class Token {

    private final String accessToken;
    private final Integer expiresIn;
    private final String type;

    public Token(String accessToken, Integer expiresIn) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.type = "Bearer";
    }

}
