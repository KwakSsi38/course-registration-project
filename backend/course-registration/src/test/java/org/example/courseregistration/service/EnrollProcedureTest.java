package org.example.courseregistration.service;

import org.example.courseregistration.course.enroll.service.EnrollService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EnrollProcedureTest {

  @Autowired
  private EnrollService enrollService;

  @Test
  public void testApplyProcedure() {
    // 테스트용 학번과 섹션ID 입력
    String result = enrollService.apply("2021001", 1L);
    System.out.println("✅ 수강신청 결과: " + result);
  }

  @Test
  public void testCancelProcedure() {
    // 테스트용 학번, 과목코드, 섹션번호 입력
    String result = enrollService.cancel("2021001", "DS101", "001");
    System.out.println("❌ 수강취소 결과: " + result);
  }
}