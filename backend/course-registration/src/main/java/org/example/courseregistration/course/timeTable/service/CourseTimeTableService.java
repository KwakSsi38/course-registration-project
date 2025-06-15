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

  public List<TimeTableDto> getTimeTable(String studentId) {
    List<TimeTableDto> timeTable = new ArrayList<>();

    String sql = "{CALL SelectTimeTable(?)}";

    try (Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall(sql)) {

      stmt.setString(1, studentId);

      boolean hasResult = stmt.execute();

      if (hasResult) {
        try (ResultSet rs = stmt.getResultSet()) {
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
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException("시간표 조회 중 오류 발생: " + e.getMessage());
    }

    return timeTable;
  }
}