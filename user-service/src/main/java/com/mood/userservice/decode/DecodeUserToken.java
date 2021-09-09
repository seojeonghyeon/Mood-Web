package com.mood.userservice.decode;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
