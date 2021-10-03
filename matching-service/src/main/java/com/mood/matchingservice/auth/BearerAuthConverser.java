package com.mood.matchingservice.auth;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import javax.servlet.http.HttpServletRequest;


@Slf4j
public class BearerAuthConverser {
    private AuthorizationExtractor authExtractor;
    public static final String AUTHORIZATION = "Authorization";
    public static final String ACCESS_TOKEN_TYPE = AuthorizationExtractor.class.getSimpleName() + ".ACCESS_TOKEN_TYPE";

    public BearerAuthConverser(AuthorizationExtractor authExtractor){
        this.authExtractor=authExtractor;
    }

    public String handle(HttpServletRequest request, Environment env) {
        log.info(">>>>>>>>Bearer Token Extracter");
        String token = authExtractor.extract(request, "Bearer");
        String userUid = Jwts.parser().setSigningKey(env.getProperty("token.secret"))
                .parseClaimsJws(token).getBody()
                .getSubject();
        log.info("User UID : "+userUid);
        return userUid;
    }
}
