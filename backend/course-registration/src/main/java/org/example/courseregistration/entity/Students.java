package org.example.courseregistration.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Students")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Students {
    @Id
    @Column(name = "s_id", length = 10, nullable = false)
    private String id;

    @Column(name = "s_name", length = 30, nullable = false)
    private String name;

    @Column(name = "s_major", length = 30)
    private String major;

    @Column(name = "s_grade", nullable = false)
    private Integer grade;

    @Column(name = "s_semester", nullable = false)
    private Integer semester;

    @Column(name = "s_status", length = 10, nullable = false)
    private String status;

    @Column(name = "s_pwd", length = 30, nullable = false)
    private String password;

    @Column(name = "s_address", length = 100)
    private String address;

    @Column(name = "s_phone", length = 15)
    private String phone;

    @Column(name = "s_email", length = 50)
    private String email;

    @Column(name = "s_bank", length = 50)
    private String bank;

    @Column(name = "s_dual_major", length = 30)
    private String dualMajor;
}

