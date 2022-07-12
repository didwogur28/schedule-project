package com.schedule.security;

import com.schedule.login.service.LoginService;
import com.schedule.login.vo.LoginVO;
import com.schedule.login.vo.UserDetailsVO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collection;

@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    protected static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    private final LoginService loginService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

        // AuthenticaionFilter에서 생성된 토큰으로부터 아이디와 비밀번호를 조회함
        String usrId = token.getName();
        String pwdNo = (String) token.getCredentials();

        LoginVO loginVO = new LoginVO();
        Collection<SimpleGrantedAuthority> authorities = null;
        UserDetailsVO userDetailsVO = null;

        try {

            loginVO = loginService.getUsrInfo(usrId);
            authorities = (Collection<SimpleGrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();

            userDetailsVO = new UserDetailsVO(loginVO, authorities);

            if (!passwordEncoder.matches(pwdNo, userDetailsVO.getPassword())) {
                throw new BadCredentialsException(userDetailsVO.getUsrId() + "Invalid password");
            }

        } catch (Exception e) {
            logger.error("authenticate 에러 : " + e.toString());
        }

        return new UsernamePasswordAuthenticationToken(userDetailsVO, pwdNo, userDetailsVO.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
