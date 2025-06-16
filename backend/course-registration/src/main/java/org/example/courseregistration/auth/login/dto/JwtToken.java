package org.example.courseregistration.auth.login.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtToken {
    private String grantType;
    private String accessToken;
}
