package org.example.courseregistration.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudentInfoResponse {
    private String id;
    private String name;
    private String grade;
    private String semester;
    private String major;
    private String dualMajor;
    private String status;
    private String phone;
    private String email;
    private String address;
    private String bank;
}
