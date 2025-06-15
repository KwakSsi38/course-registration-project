package org.example.courseregistration.course.enroll.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnrollRequestDto {
  private String studentId;
  private String sectionId;

  public String getStudentId() {
    return studentId;
  }

  public Number getSectionId() {
    return sectionId;
  }

  public String getCourseIdNo() {
    return null;
  }

  public String getSectionNo() {
    return null;
  }
}