package org.example.courseregistration.course.getCourses.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseSearchCondition {
  private String courseName; // 과목명
  private String courseIdNo; // 과목번호
  private String major; // 전공
  private String professorName; // 교수명
  private Integer credit; // 학점
}