package com.mood.lockservice.decode;

import io.jsonwebtoken.Jwts;
import org.springframework.core.env.Environment;

public class DecodeUserToken {

    public String getUserUidByUserToken(String userToken, Environment env){
        if(userToken.isEmpty()){
            return null;
        }
        String userUid = null;
        try {
            userUid = Jwts.parser().setSigningKey(env.getProperty("token.secret"))
                    .parseClaimsJws(userToken).getBody()
                    .getSubject();
        }catch (Exception ex){
            return null;
        }
        if(userUid ==null || userUid.isEmpty()){
            return null;
        }
        return userUid;
    }
}
