package org.example.courseregistration.course.enroll.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 임포트 추가

@Service
@Transactional // 클래스 레벨에 @Transactional 추가
public class EnrollService {

  @PersistenceContext
  private EntityManager entityManager;

  // ✅ 수강신청
  // 이 메서드 자체가 트랜잭션 경계가 됩니다.
  public String apply(String studentId, String sectionId) {
    try {
      StoredProcedureQuery query = entityManager
          .createStoredProcedureQuery("InsertEnroll");

      query.registerStoredProcedureParameter("sStudentId", String.class, ParameterMode.IN);
      query.registerStoredProcedureParameter("nSectionId", Long.class, ParameterMode.IN);
      query.registerStoredProcedureParameter("result", String.class, ParameterMode.OUT);

      query.setParameter("sStudentId", studentId);
      query.setParameter("nSectionId", Long.valueOf(sectionId)); // sectionId가 String이므로 Long으로 변환

      query.execute();
      return (String) query.getOutputParameterValue("result");

    } catch (Exception e) {
      throw new RuntimeException("프로시저 실행 중 오류 발생: " + e.getMessage(), e);
      // return "프로시저 실행 중 오류 발생: " + e.getMessage(); // 이 줄 대신 throw 사용
    }
  }

  // ✅ 수강 취소 (DeleteEnroll 프로시저)
  // 이 메서드 자체가 트랜잭션 경계가 됩니다.
  public String cancel(String studentId, String courseIdNo, String sectionNo) {
    try {
      StoredProcedureQuery query = entityManager
          .createStoredProcedureQuery("DeleteEnroll");

      query.registerStoredProcedureParameter("sStudentId", String.class, ParameterMode.IN);
      query.registerStoredProcedureParameter("vCourseIdNo", String.class, ParameterMode.IN);
      query.registerStoredProcedureParameter("vSectionNo", String.class, ParameterMode.IN); // String으로 유지
      query.registerStoredProcedureParameter("result", String.class, ParameterMode.OUT);

      query.setParameter("sStudentId", studentId);
      query.setParameter("vCourseIdNo", courseIdNo);
      query.setParameter("vSectionNo", sectionNo); // String으로 이미 받으므로 그대로 사용

      query.execute();
      return (String) query.getOutputParameterValue("result");

    } catch (Exception e) {
      // 트랜잭션이 롤백될 수 있도록 RuntimeException을 던지는 것이 좋습니다.
      // 현재는 String 반환이므로, 호출하는 쪽에서 이 오류를 처리해야 합니다.
      throw new RuntimeException("삭제 프로시저 실행 중 오류 발생: " + e.getMessage(), e);
      // return "삭제 프로시저 실행 중 오류 발생: " + e.getMessage(); // 이 줄 대신 throw 사용
    }
  }
}