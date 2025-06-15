package org.example.courseregistration.course.getCourses.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseSectionDto {

  private Long sectionId; // 섹션 ID (PK)
  private String courseIdNo; // 과목번호 (c_id_no)
  private String courseName; // 과목명 (c_id)
  private int credit; // 학점 (c_unit)
  private String major; // 전공 (c_major)
  private int section; // 분반 번호 (se_section)
  private String classTime; // 수업 시간 (se_time)
  private int capacity; // 수강 정원 (se_capacity)
  private String classroom; // 강의실 위치 (se_classroom)
  private String professorName;
}