// ✅ 경로: src/main/java/org/example/courseregistration/dto/TimeTableDto.java
package org.example.courseregistration.course.timeTable.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TimeTableDto {
  private String courseIdNo; // 과목번호
  private String courseName; // 과목명
  private String section; // 분반
  private int credits; // 학점
  private String classroom; // 장소
  private String time; // 교시
}