CREATE OR REPLACE PROCEDURE SelectTimeTable (
  sStudentId IN Enroll.s_id%TYPE,
  p_cursor    OUT SYS_REFCURSOR
) IS
  -- ✅ 연도/학기 자동 계산
  nYear     NUMBER := Date2EnrollYear(SYSDATE);
  nSemester NUMBER := Date2EnrollSemester(SYSDATE);

BEGIN
  -- ✅ 커서를 바로 반환
  OPEN p_cursor FOR
    SELECT *
    FROM V_StudentTimetable
    WHERE student_id = sStudentId
      AND year = nYear
      AND semester = nSemester;
END;
/