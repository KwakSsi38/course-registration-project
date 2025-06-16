package org.example.courseregistration.auth.login.dto;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String studentId;
    private String password;
}
