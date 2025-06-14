// ✅ 경로: dto/DeleteRequestDto.java

package org.example.courseregistration.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteRequestDto {
    private String studentId;
    private String courseIdNo;
    private String sectionNo;

    public String getStudentId() {
        return studentId;
    }

    public String getCourseIdNo() {
        return courseIdNo;
    }

    public String getSectionNo() {
        return sectionNo;
    }
}