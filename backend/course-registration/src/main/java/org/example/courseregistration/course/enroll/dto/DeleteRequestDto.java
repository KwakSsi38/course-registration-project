// ✅ 경로: dto/DeleteRequestDto.java

package org.example.courseregistration.course.enroll.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteRequestDto {
  private String courseIdNo;
  private String sectionNo;

  public String getStudentId() {
    // return studentId;
    return null;
  }

  public String getCourseIdNo() {
    return courseIdNo;
  }

  public String getSectionNo() {
    return sectionNo;
  }
}