package com.example.oceananalyse.controller;

import com.example.oceananalyse.entity.UserInfo;
import com.example.oceananalyse.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserInfo user) {
        try {
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                return buildErrorResponse("用户名不能为空", HttpStatus.BAD_REQUEST);
            }
            
            if (user.getUsername().length() < 2 || user.getUsername().length() > 20) {
                return buildErrorResponse("用户名长度必须在2-20个字符之间", HttpStatus.BAD_REQUEST);
            }
            
            if (!user.getUsername().matches("^[a-zA-Z0-9_]+$")) {
                return buildErrorResponse("用户名只能包含字母、数字和下划线", HttpStatus.BAD_REQUEST);
            }
            
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                return buildErrorResponse("密码不能为空", HttpStatus.BAD_REQUEST);
            }
            
            if (user.getPassword().length() < 6) {
                return buildErrorResponse("密码长度至少6个字符", HttpStatus.BAD_REQUEST);
            }
            
            UserInfo registeredUser = userInfoService.register(user);
            Map<String, Object> result = new HashMap<>();
            result.put("user", registeredUser);
            result.put("message", "注册成功");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        if (username == null || username.trim().isEmpty()) {
            return buildErrorResponse("用户名不能为空", HttpStatus.BAD_REQUEST);
        }
        
        if (password == null || password.trim().isEmpty()) {
            return buildErrorResponse("密码不能为空", HttpStatus.BAD_REQUEST);
        }
        
        return userInfoService.login(username, password)
                .map(user -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("user", user);
                    result.put("message", "登录成功");
                    return ResponseEntity.ok(result);
                })
                .orElseGet(() -> {
                    return buildErrorResponse("用户名或密码错误", HttpStatus.UNAUTHORIZED);
                });
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer role) {
        List<UserInfo> users = userInfoService.getAllUsers();
        
        // 搜索过滤
        if (username != null && !username.isEmpty()) {
            users = users.stream()
                    .filter(u -> u.getUsername().contains(username))
                    .collect(java.util.stream.Collectors.toList());
        }
        if (role != null) {
            users = users.stream()
                    .filter(u -> role.equals(u.getUserRole()))
                    .collect(java.util.stream.Collectors.toList());
        }
        
        // 分页处理（前端从1开始）
        int totalElements = users.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        
        // 转换为从0开始的索引
        int start = (page - 1) * size;
        int end = Math.min(start + size, totalElements);
        
        if (start >= totalElements) {
            users = new java.util.ArrayList<>();
        } else {
            users = users.subList(start, end);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("content", users);
        result.put("totalElements", totalElements);
        result.put("totalPages", totalPages);
        result.put("number", page);
        result.put("size", size);
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userInfoService.getUserById(id)
                .map(user -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("user", user);
                    return ResponseEntity.ok(result);
                })
                .orElseGet(() -> buildErrorResponse("用户不存在", HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserInfo userDetails) {
        try {
            if (userDetails.getUsername() != null && (userDetails.getUsername().length() < 2 || userDetails.getUsername().length() > 20)) {
                return buildErrorResponse("用户名长度必须在2-20个字符之间", HttpStatus.BAD_REQUEST);
            }
            
            UserInfo updatedUser = userInfoService.updateUser(id, userDetails);
            Map<String, Object> result = new HashMap<>();
            result.put("user", updatedUser);
            result.put("message", "更新成功");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userInfoService.deleteUser(id);
            Map<String, Object> result = new HashMap<>();
            result.put("message", "删除成功");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> error = new HashMap<>();
        error.put("message", message);
        error.put("status", status.value());
        error.put("error", status.getReasonPhrase());
        return ResponseEntity.status(status).body(error);
    }
}
