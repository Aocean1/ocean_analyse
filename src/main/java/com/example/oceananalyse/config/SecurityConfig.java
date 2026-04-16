package com.example.oceananalyse.config;

import com.example.oceananalyse.entity.UserInfo;
import com.example.oceananalyse.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserInfoRepository userInfoRepository;
    
    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                UserInfo user = userInfoRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
                
                String role = getRoleName(user.getUserRole());
                return new User(
                    user.getUsername(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                );
            }
        }).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
            // 公开访问的页面
            .antMatchers("/", "/index", "/home", "/login", "/core-samples", "/analysis", "/reports").permitAll()
            // 静态资源
            .antMatchers("/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
            // 登录和注册接口
            .antMatchers("/api/users/login", "/api/users/register").permitAll()
            
            // ========== 岩心数据查看 - 公开访问 ==========
            .antMatchers("/api/core-samples").permitAll()
            .antMatchers("/api/core-samples/{id}").permitAll()
            .antMatchers("/api/core-samples/search/**").permitAll()
            
            // ========== 岩心数据录入 - 管理员、教师和普通用户可访问 ==========
            .antMatchers(org.springframework.http.HttpMethod.POST, "/api/core-samples").hasAnyRole("ADMIN", "TEACHER", "NORMAL")
            
            // ========== 岩心数据修改 - 管理员、教师和普通用户可访问 ==========
            .antMatchers(org.springframework.http.HttpMethod.PUT, "/api/core-samples/**").hasAnyRole("ADMIN", "TEACHER", "NORMAL")
            
            // ========== 岩心数据删除 - 仅管理员可访问 ==========
            .antMatchers(org.springframework.http.HttpMethod.DELETE, "/api/core-samples/**").hasRole("ADMIN")
            
            // ========== 数据分析查看 - 公开访问 ==========
            .antMatchers("/api/image-analysis").permitAll()
            .antMatchers("/api/image-analysis/{id}").permitAll()
            
            // ========== 数据分析上传/重新分析 - 管理员、教师和普通用户可访问 ==========
            .antMatchers(org.springframework.http.HttpMethod.POST, "/api/image-analysis/upload").hasAnyRole("ADMIN", "TEACHER", "NORMAL")
            .antMatchers("/api/image-analysis/reanalyze/**").hasAnyRole("ADMIN", "TEACHER", "NORMAL")
            .antMatchers("/api/image-analysis/batch/**").hasAnyRole("ADMIN", "TEACHER", "NORMAL")
            .antMatchers("/api/image-analysis/core/**").hasAnyRole("ADMIN", "TEACHER", "NORMAL")
            
            // ========== 报告生成 - 公开访问 ==========
            .antMatchers("/api/reports").permitAll()
            .antMatchers("/api/reports/{id}").permitAll()
            .antMatchers("/api/reports/core/**").permitAll()
            .antMatchers("/api/reports/generate/**").permitAll()
            
            // ========== 用户管理 - 仅管理员可访问 ==========
            .antMatchers("/users").hasRole("ADMIN")
            .antMatchers("/api/users").hasRole("ADMIN")
            .antMatchers("/api/users/**").hasRole("ADMIN")
            
            // 其他所有请求公开访问
            .anyRequest().permitAll()
            // 配置表单登录
            .and()
            .formLogin()
            .loginPage("/login")
            .defaultSuccessUrl("/", true)
            .permitAll()
            .and()
            .logout()
            .logoutSuccessUrl("/login")
            .permitAll()
            .and()
            .exceptionHandling()
            .accessDeniedHandler(customAccessDeniedHandler);
    }

    private String getRoleName(Integer role) {
        if (role == null) return "NORMAL";
        switch (role) {
            case 1: return "NORMAL";
            case 2: return "TEACHER";
            case 3: return "ADMIN";
            default: return "NORMAL";
        }
    }
}
