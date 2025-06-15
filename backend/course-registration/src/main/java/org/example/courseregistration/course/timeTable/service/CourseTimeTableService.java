package org.example.courseregistration.course.timeTable.service;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.course.timeTable.dto.TimeTableDto;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseTimeTableService {

  private final DataSource dataSource;

  public List<TimeTableDto> getTimeTable(String studentId, int year, int semester) {
    List<TimeTableDto> timeTable = new ArrayList<>();

    String sql = "{CALL SelectTimeTable(?, ?, ?)}";

    try (Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall(sql)) {

      stmt.setString(1, studentId);
      stmt.setInt(2, year);
      stmt.setInt(3, semester);

      boolean hasResult = stmt.execute();

      if (hasResult) {
        try (ResultSet rs = stmt.getResultSet()) {
          while (rs.next()) {
            timeTable.add(new TimeTableDto(
                rs.getString("과목번호"),
                rs.getString("과목명"),
                rs.getString("분반"),
                rs.getInt("학점"),
                rs.getString("장소"),
                rs.getString("교시")));
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException("시간표 조회 중 오류 발생: " + e.getMessage());
    }

    return timeTable;
  }
}