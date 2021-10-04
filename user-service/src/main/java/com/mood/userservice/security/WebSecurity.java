package com.mood.userservice.security;

import com.mood.userservice.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration //다른 빈보다 우선 순위로 부여하게 됨
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
    private UserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private Environment env;

    public WebSecurity(Environment env, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.env = env;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    //권한에 관련된 부분
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests().antMatchers("/actuator/**").permitAll();
        http.authorizeRequests().antMatchers("/**")
                .permitAll()
                .and()
                .addFilter(getAuthenticationFilter());
//                .formLogin().successHandler(getLoginSuccessHandler());
        http.headers().frameOptions().disable();
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception{
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager(), userService, env);
        return authenticationFilter;
    }
    private LoginSuccessHandler getLoginSuccessHandler() throws Exception{
        LoginSuccessHandler loginSuccessHandler = new LoginSuccessHandler(userService, env);
        return loginSuccessHandler;
    }

    //인증에 관련된 부분
    //select pwd from users where email=?
    //db_pwd(encrypted)==input_pwd(encrypted)
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //userDatilService에서 Select pwd~~=?처리를 하는데 userService를 통해서 DB의 pwd를 가져오기 됨.
        // 인코딩된 pwd와 입력받은 pwd를 비교하기 위한 패스워드 인코더를 빈으로부터 가져오게됨.
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }
}
