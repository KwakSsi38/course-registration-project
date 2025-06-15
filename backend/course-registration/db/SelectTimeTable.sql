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


CREATE OR REPLACE PROCEDURE SelectTimeTable (
    sStudentId IN VARCHAR2, -- [IN] 학생 학번 입력
    nYear      IN NUMBER,   -- [IN] 해당 연도 입력
    nSemester  IN NUMBER    -- [IN] 해당 학기 입력
)
    IS
    -- 커서 정의: 뷰에서 조건에 맞는 시간표 조회
    CURSOR timetable_cur IS
        SELECT time AS 교시,
               course_id AS 과목번호,
               course_name AS 과목명,
               section AS 분반,
               credit AS 학점,
               classroom AS 장소
        FROM V_StudentTimetable v
                 JOIN sections s ON v.course_id = s.c_id_no AND v.section = s.se_section
        WHERE v.student_id = sStudentId
          AND s.se_year = nYear
          AND s.se_semester = nSemester;

    -- 커서에서 가져올 변수들
    vTime        VARCHAR2(100);
    vCourseId    VARCHAR2(20);
    vCourseName  VARCHAR2(100);
    vSection     VARCHAR2(10);
    vCredits     NUMBER;
    vClassroom   VARCHAR2(50);

    -- 합계 계산용
    vTotalSubjects NUMBER := 0;
    vTotalCredits  NUMBER := 0;
BEGIN
    DBMS_OUTPUT.PUT_LINE('<< 수강신청 시간표 >>');
    DBMS_OUTPUT.PUT_LINE('교시 | 과목번호 | 과목명 | 분반 | 학점 | 장소');
    DBMS_OUTPUT.PUT_LINE('---------------------------------------------------------');

    -- 커서 실행
    OPEN timetable_cur;
    LOOP
        FETCH timetable_cur INTO vTime, vCourseId, vCourseName, vSection, vCredits, vClassroom;
        EXIT WHEN timetable_cur%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE(
                vTime || ' | ' || vCourseId || ' | ' || vCourseName || ' | ' ||
                vSection || ' | ' || vCredits || ' | ' || vClassroom
        );

        vTotalSubjects := vTotalSubjects + 1;
        vTotalCredits  := vTotalCredits + vCredits;
    END LOOP;
    CLOSE timetable_cur;

    -- 합계 출력
    DBMS_OUTPUT.PUT_LINE('---------------------------------------------------------');
    DBMS_OUTPUT.PUT_LINE('총 신청 과목 수: ' || vTotalSubjects);
    DBMS_OUTPUT.PUT_LINE('총 신청 학점 수: ' || vTotalCredits);
END;
/

