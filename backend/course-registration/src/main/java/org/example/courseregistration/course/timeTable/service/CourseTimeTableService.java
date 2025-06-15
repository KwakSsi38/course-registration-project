package org.example.courseregistration.course.timeTable.service;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.course.timeTable.dto.TimeTableDto;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
// OracleTypes.CURSOR를 사용하기 위한 임포트
// ojdbc 드라이버가 Classpath에 있어야 합니다.
import oracle.jdbc.OracleTypes;

@Service
@RequiredArgsConstructor
public class CourseTimeTableService {

  private final DataSource dataSource;

  public List<TimeTableDto> getTimeTable(String studentId) {
    List<TimeTableDto> timeTable = new ArrayList<>();

    // 프로시저 호출 시 입력 파라미터 (?)와 출력 파라미터 (?) 두 개를 명시합니다.
    String sql = "{CALL SelectTimeTable(?, ?)}";

    try (Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall(sql)) {

      // 1. 첫 번째 파라미터: studentId (IN 파라미터)
      stmt.setString(1, studentId);

      // 2. 두 번째 파라미터: SYS_REFCURSOR (OUT 파라미터) 등록
      // oracle.jdbc.OracleTypes.CURSOR를 사용합니다.
      stmt.registerOutParameter(2, OracleTypes.CURSOR);

      // 프로시저 실행
      stmt.execute();

      // OUT 파라미터로 반환된 커서에서 ResultSet을 가져옵니다.
      try (ResultSet rs = (ResultSet) stmt.getObject(2)) {
        // ResultSet에서 데이터를 읽어 TimeTableDto 리스트에 추가
        while (rs.next()) {
          timeTable.add(new TimeTableDto(
              rs.getString("courseIdNo"),
              rs.getString("courseName"),
              rs.getString("section"),
              rs.getInt("credit"),
              rs.getString("major"),
              rs.getString("professorName"),
              rs.getString("time"),
              rs.getString("classroom")));
        }
      }
    } catch (SQLException e) {
      // 오류 발생 시 스택 트레이스를 출력하고 RuntimeException을 발생시킵니다.
      e.printStackTrace();
      throw new RuntimeException("시간표 조회 중 오류 발생: " + e.getMessage());
    }

    return timeTable;
  }
}
