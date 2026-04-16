package com.example.oceananalyse.entity;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "user_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", length = 20, unique = true, nullable = false)
    private String username;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "user_role")
    private Integer userRole;

    @Column(name = "created_at")
    private java.util.Date createdAt;

    public static final int ROLE_NORMAL = 1;
    public static final int ROLE_TEACHER = 2;
    public static final int ROLE_ADMIN = 3;
}