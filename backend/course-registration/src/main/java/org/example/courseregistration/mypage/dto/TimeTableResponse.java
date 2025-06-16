package org.example.courseregistration.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TimeTableResponse {
    private String courseIdNo;
    private String courseName;
    private Integer section;
    private Float score;
    private String major;
    private String professorName;
    private String time;
    private String classroom;
}
