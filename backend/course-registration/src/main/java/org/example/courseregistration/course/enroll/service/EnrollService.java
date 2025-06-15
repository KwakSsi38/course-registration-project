package org.example.courseregistration.course.enroll.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Service;

@Service
public class EnrollService {

  @PersistenceContext
  private EntityManager entityManager;

  // ✅ 수강신청
  public String apply(String studentId, String sectionId) {
    try {
      StoredProcedureQuery query = entityManager
          .createStoredProcedureQuery("InsertEnroll");

      query.registerStoredProcedureParameter("sStudentId", String.class, ParameterMode.IN);
      query.registerStoredProcedureParameter("nSectionId", Long.class, ParameterMode.IN);
      query.registerStoredProcedureParameter("result", String.class, ParameterMode.OUT);

      query.setParameter("sStudentId", studentId);
      query.setParameter("nSectionId", sectionId);

      query.execute();
      return (String) query.getOutputParameterValue("result");

    } catch (Exception e) {
      return "프로시저 실행 중 오류 발생: " + e.getMessage();
    }
  }

  // ✅ 수강 취소 (DeleteEnroll 프로시저)
  public String cancel(String studentId, String courseIdNo, String sectionNo) {
    try {
      StoredProcedureQuery query = entityManager
          .createStoredProcedureQuery("DeleteEnroll");

      query.registerStoredProcedureParameter("sStudentId", String.class, ParameterMode.IN);
      query.registerStoredProcedureParameter("vCourseIdNo", String.class, ParameterMode.IN);
      query.registerStoredProcedureParameter("vSectionNo", String.class, ParameterMode.IN);
      query.registerStoredProcedureParameter("result", String.class, ParameterMode.OUT);

      query.setParameter("sStudentId", studentId);
      query.setParameter("vCourseIdNo", courseIdNo);
      query.setParameter("vSectionNo", sectionNo);

      query.execute();
      return (String) query.getOutputParameterValue("result");

    } catch (Exception e) {
      return "삭제 프로시저 실행 중 오류 발생: " + e.getMessage();
    }
  }
}