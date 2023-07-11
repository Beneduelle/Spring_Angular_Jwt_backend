package com.example.SpringJWT.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.SpringJWT.domain.UserPrincipal;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

import static com.example.SpringJWT.constant.SecurityConstant.*;

public class JWTTokenProvider {

    @Value("${jwt.secret}")
    private String secret; //salt(random string)

    public String generateJwtToken(UserPrincipal userPrincipal) {
        String[] claims = getClaimsFromUser(userPrincipal);
        return JWT.create()
                .withIssuer(GET_ARRAYS_LLC) //author of token
                .withAudience(GET_ARRAYS_ADMINISTRATION) //company
                .withIssuedAt(new Date()) //release date
                .withSubject(userPrincipal.getUsername())
                .withArrayClaim(AUTHORITIES, claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    private String[] getClaimsFromUser(UserPrincipal userPrincipal) {

    }
}
