package com.example.oceananalyse.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, 
                       AccessDeniedException accessDeniedException) throws IOException {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "未登录用户";
        String role = getCurrentRole(auth);
        
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("success", false);
        responseBody.put("code", 403);
        responseBody.put("message", "您当前角色为【" + role + "】，没有权限执行此操作");
        responseBody.put("detail", "请联系管理员获取更高权限");
        responseBody.put("username", username);
        responseBody.put("role", role);
        
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
    
    private String getCurrentRole(Authentication auth) {
        if (auth == null) {
            return "未登录";
        }
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "管理员";
        }
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"))) {
            return "教师";
        }
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_NORMAL"))) {
            return "普通用户";
        }
        return "未知角色";
    }
}
