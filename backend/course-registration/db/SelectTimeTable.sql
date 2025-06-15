CREATE OR REPLACE PROCEDURE SelectTimeTable (
  sStudentId IN Enroll.s_id%TYPE
) IS
  -- ✅ 연도/학기 자동 계산
  nYear     NUMBER := Date2EnrollYear(SYSDATE);
  nSemester NUMBER := Date2EnrollSemester(SYSDATE);

  -- ✅ ROWTYPE: 뷰 전체 행 사용
  vRow V_StudentTimetable%ROWTYPE;

  -- ✅ 합계 변수
  vTotalSubjects NUMBER := 0;
  vTotalCredits  NUMBER := 0;

  -- ✅ 커서 정의
  CURSOR timetable_cur IS
    SELECT *
    FROM V_StudentTimetable
    WHERE student_id = sStudentId
      AND year = nYear
      AND semester = nSemester;
BEGIN
  DBMS_OUTPUT.PUT_LINE('<< 수강신청 시간표 (' || nYear || '-' || nSemester || ') >>');
  DBMS_OUTPUT.PUT_LINE('과목번호 | 과목명 | 분반 | 학점 | 전공 | 교수 | 시간 | 장소');
  DBMS_OUTPUT.PUT_LINE('----------------------------------------------------------------');

  OPEN timetable_cur;
  LOOP
    FETCH timetable_cur INTO vRow;
    EXIT WHEN timetable_cur%NOTFOUND;

    DBMS_OUTPUT.PUT_LINE(
      vRow.courseIdNo || ' | ' ||
      vRow.courseName || ' | ' ||
      vRow.section     || ' | ' ||
      vRow.credit      || ' | ' ||
      vRow.major       || ' | ' ||
      vRow.professorName || ' | ' ||
      vRow.time        || ' | ' ||
      vRow.classroom
    );

    vTotalSubjects := vTotalSubjects + 1;
    vTotalCredits  := vTotalCredits + vRow.credit;
  END LOOP;
  CLOSE timetable_cur;

  DBMS_OUTPUT.PUT_LINE('----------------------------------------------------------------');
  DBMS_OUTPUT.PUT_LINE('총 수강 과목 수: ' || vTotalSubjects);
  DBMS_OUTPUT.PUT_LINE('총 수강 학점 수: ' || vTotalCredits);
END;
/