// ✅ 경로: src/main/java/org/example/courseregistration/dto/TimeTableDto.java
package org.example.courseregistration.sub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TimeTableDto {
    private String time;       // 교시
    private String courseId;   // 과목번호
    private String courseName; // 과목명
    private String sectionNo;  // 분반
    private int credits;       // 학점
    private String classroom;  // 장소
}