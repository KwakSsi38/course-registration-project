package org.example.courseregistration.course.enroll.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.example.courseregistration.course.enroll.dto.EnrollResponseDto; // EnrollResponseDto 임포트
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EnrollService {

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * 수강 신청 프로시저 호출 및 결과 반환
   * 
   * @param studentId 학생 학번
   * @param sectionId 섹션 ID
   * @return 수강 신청 결과 메시지와 남은 여석을 포함하는 DTO
   */
  public EnrollResponseDto apply(String studentId, Long sectionId) {
    // PL/SQL 프로시저가 자체적으로 예외를 처리하고 'result' OUT 파라미터로 메시지를 반환하므로,
    // 여기서는 광범위한 try-catch 블록으로 프로시저 메시지를 덮어쓰지 않습니다.
    // JDBC/JPA 호출 자체에서 발생하는 시스템 오류는 Spring의 @Transactional이 처리합니다.
    StoredProcedureQuery query = entityManager
        .createStoredProcedureQuery("InsertEnroll");

    // IN 파라미터 등록
    query.registerStoredProcedureParameter("sStudentId", String.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("nSectionId", Long.class, ParameterMode.IN);

    // OUT 파라미터 등록
    query.registerStoredProcedureParameter("result", String.class, ParameterMode.OUT);
    query.registerStoredProcedureParameter("nLeftSeats", Integer.class, ParameterMode.OUT); // Integer로 등록

    // IN 파라미터 값 설정
    query.setParameter("sStudentId", studentId);
    query.setParameter("nSectionId", sectionId);

    // 프로시저 실행
    query.execute();

    // OUT 파라미터 값 가져오기
    String message = (String) query.getOutputParameterValue("result");
    Integer remainingSeats = (Integer) query.getOutputParameterValue("nLeftSeats");

    // DTO 객체로 반환
    return new EnrollResponseDto(message, remainingSeats);
  }

  /**
   * 수강 취소 프로시저 호출 (기존 로직 유지)
   */
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
      // 이 catch 블록은 DeleteEnroll 프로시저가 Oracle 오류를 발생시키거나
      // JDBC/JPA 호출 자체에 문제가 있을 때만 발동합니다.
      throw new RuntimeException("삭제 프로시저 실행 중 오류 발생: " + e.getMessage(), e);
    }
  }
}