package org.example.courseregistration.sub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "STUDENTS", schema = "DB2110102")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Students {
    @Id
    @Column(name = "S_ID", length = 10, nullable = false)
    private String id;

    @Column(name = "S_NAME", length = 30, nullable = false)
    private String name;

    @Column(name = "S_MAJOR", length = 30)
    private String major;

    @Column(name = "S_GRADE", nullable = false)
    private Integer grade;

    @Column(name = "S_SEMESTER", nullable = false)
    private Integer semester;

    @Column(name = "S_STATUS", length = 10, nullable = false)
    private String status;

    @Column(name = "S_PWD", length = 30, nullable = false)
    private String password;

    @Column(name = "S_ADDRESS", length = 100)
    private String address;

    @Column(name = "S_PHONE", length = 15)
    private String phone;

    @Column(name = "S_EMAIL", length = 50)
    private String email;

    @Column(name = "S_BANK", length = 50)
    private String bank;

    @Column(name = "S_DUAL_MAJOR", length = 30)
    private String dualMajor;

    public void setPassword(String password) {
        this.password = password;
    }
}




