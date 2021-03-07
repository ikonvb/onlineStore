package com.konstantinbulygin.onlinestore.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(@Qualifier("userDetailsServiceRepository") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/category/**")
                .hasAnyRole("USER", "ADMIN")
                .antMatchers("/admin/**")
                .hasAnyRole("ADMIN")
//                .antMatchers("/**").hasAnyRole("USER")
                .antMatchers("/").permitAll()
                .and()
                .formLogin()
                .loginPage("/login")
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .and()
                .exceptionHandling()
                .accessDeniedPage("/");

    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(new BCryptPasswordEncoder());
    }
}
