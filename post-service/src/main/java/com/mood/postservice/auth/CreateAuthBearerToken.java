package com.mood.postservice.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.core.env.Environment;

import java.util.Date;

public class CreateAuthBearerToken {
    public String createToken(Environment env, String userUid){
        return Jwts.builder()
                .setSubject(userUid)
                .setExpiration(new Date(System.currentTimeMillis()+Long.parseLong(env.getProperty("token.expiration_time"))))
                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
                .compact();
    }
}
