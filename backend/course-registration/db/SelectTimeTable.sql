CREATE OR REPLACE PROCEDURE SelectTimeTable (
  sStudentId IN Enroll.s_id%TYPE,
  p_cursor    OUT SYS_REFCURSOR
) IS
  -- ✅ 연도/학기 자동 계산
  nYear     NUMBER := Date2EnrollYear(SYSDATE);
  nSemester NUMBER := Date2EnrollSemester(SYSDATE);

  -- ✅ ROWTYPE: 뷰 전체 행 사용
  vRow V_StudentTimetable%ROWTYPE;

  -- ✅ 합계 변수 (사용 X → 필요시 Java에서 집계)
  vTotalSubjects NUMBER := 0;
  vTotalCredits  NUMBER := 0;
BEGIN
  -- ✅ 커서를 바로 반환
  OPEN p_cursor FOR
    SELECT *
    FROM V_StudentTimetable
    WHERE student_id = sStudentId
      AND year = nYear
      AND semester = nSemester;
END;
