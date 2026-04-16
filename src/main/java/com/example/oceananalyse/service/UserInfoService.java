package com.example.oceananalyse.service;

import com.example.oceananalyse.entity.UserInfo;
import com.example.oceananalyse.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class UserInfoService {

    @Autowired
    private UserInfoRepository userInfoRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserInfo register(UserInfo user) {
        if (userInfoRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 强制新用户注册为普通用户，不允许注册为管理员或教师
        user.setUserRole(UserInfo.ROLE_NORMAL);
        return userInfoRepository.save(user);
    }

    public Optional<UserInfo> login(String username, String password) {
        Optional<UserInfo> user = userInfoRepository.findByUsername(username);
        if (user.isPresent()) {
            String storedPassword = user.get().getPassword();
            if (storedPassword != null && storedPassword.length() == 60 && storedPassword.startsWith("$2a$")) {
                if (passwordEncoder.matches(password, storedPassword)) {
                    return user;
                }
            } else {
                if (password.equals(storedPassword)) {
                    return user;
                }
            }
        }
        return Optional.empty();
    }

    public List<UserInfo> getAllUsers() {
        return userInfoRepository.findAll();
    }

    public Optional<UserInfo> getUserById(Long id) {
        return userInfoRepository.findById(id);
    }

    public UserInfo updateUser(Long id, UserInfo userDetails) {
        UserInfo user = userInfoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (!user.getUsername().equals(userDetails.getUsername()) 
                && userInfoRepository.existsByUsername(userDetails.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        user.setUsername(userDetails.getUsername());
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        user.setUserRole(userDetails.getUserRole());
        return userInfoRepository.save(user);
    }

    public void deleteUser(Long id) {
        userInfoRepository.deleteById(id);
    }
}