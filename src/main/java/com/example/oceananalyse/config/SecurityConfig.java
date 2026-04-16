package com.example.oceananalyse.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/", "/index", "/home", "/login", "/users", "/core-samples", "/analysis", "/reports", "/api/**").permitAll()
            .antMatchers("/css/**", "/js/**", "/images/**").permitAll()
            .anyRequest().permitAll();
    }
}
