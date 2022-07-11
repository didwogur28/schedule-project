package com.schedule.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) throws Exception {

        web.ignoring().antMatchers("/static/**", "/resources/**");
        web.debug(false);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable().authorizeRequests()
                .antMatchers("/about").authenticated()          // /about 요청에 대해서는 로그인을 요구함
                .antMatchers("/admin").hasRole("ROLE_ADMIN")    // /admin 요청에 대해서는 ROLE_ADMIN 역할을 가지고 있어야 함
                .anyRequest().permitAll()                                   // 나머지 요청에 대해서는 로그인을 요구하지 않음
                .and()
                // 로그인하는 경우에 대해 설정함
                .formLogin()
                .loginPage("/login/login")            // 로그인 페이지를 제공하는 URL을 설정함
                .successForwardUrl("/main/main")      // 로그인 성공 URL을 설정함
                .failureForwardUrl("/login/login")    // 로그인 실패 URL을 설정함
                .permitAll()
                .and()
                .addFilterBefore(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter() throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager());
        customAuthenticationFilter.setFilterProcessesUrl("/login/login");
        customAuthenticationFilter.setAuthenticationSuccessHandler(customLoginSuccessHandler());
        customAuthenticationFilter.afterPropertiesSet();
        return customAuthenticationFilter;
    }

    @Bean
    public CustomLoginSuccessHandler customLoginSuccessHandler() {
        return new CustomLoginSuccessHandler();
    }
}
