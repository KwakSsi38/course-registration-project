package org.example.courseregistration.course.enroll.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Getter, Setter, toString, equals, hashCode 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자 자동 생성
public class EnrollResponseDto {
  private String message;
  private Integer remainingSeats; // PL/SQL의 NUMBER 타입은 Java의 Integer 또는 Long으로 매핑
}