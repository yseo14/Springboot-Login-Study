package com.example.config;

import com.example.domain.enums.UserRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;


@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/security-login/info").authenticated()    //authenticated: 해당 url에 진입하기 위해서 인증, 로그인이 필요함
                        .requestMatchers("/security-login/admin/**").hasAuthority(UserRole.ADMIN.name())    //해당 url에 진입하기 위해서 인가(Authorization)가 필요
                        .anyRequest().permitAll())  //  위의 url을 제외한 나머지는 인증, 인가 없이 통과

                .formLogin((form) -> form   // Form Login 방식 사용
                        .usernameParameter("loginId")   //  로그인할 때 사용되는 ID를 적는다. username으로 로그인 시 따로 적어주지 않아도 됨.
                        .passwordParameter("password")  //  로그인할 때 사용되는 password를 적는다. password로 로그인을 한다면 따로 적어줄 필요 없음.
                        .loginPage("/security-login/login") //  로그인 페이지 url
                        .defaultSuccessUrl("/security-login")   //로그인 성공 시 이동할 url
                        .failureUrl("/security-login/login"))   //  로그인 실패 시 이동할 url

                .logout((form) -> form  //  로그아웃에 대한 정보
                        .logoutUrl("/security-login/logout")
                        .invalidateHttpSession(true).deleteCookies("JSESSIONID"))
                .build();
    }

}