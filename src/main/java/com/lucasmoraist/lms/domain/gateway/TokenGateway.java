package com.lucasmoraist.lms.domain.gateway;

import com.lucasmoraist.lms.domain.model.Identity;
import com.lucasmoraist.lms.domain.model.Token;

public interface TokenGateway {

    Token generateToken(Identity identity);
    String getSubjectFromToken(String token);
    String getClaimFromToken(String token, String claim);

}
