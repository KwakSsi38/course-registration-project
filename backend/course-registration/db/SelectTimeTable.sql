/*패러미터로 입력한 학번, 년도, 학기에 해당하는 수강신청 시간표를 보여준다.
시간표 정보로 교시, 과목번호, 과목명, 분반, 학점, 장소를 보여줌
총 신청 과목수와 총 학점을 보여줌*/

CREATE OR REPLACE PROCEDURE SelectTimeTable (
    sStudentId IN VARCHAR2, -- [IN] 학생 학번 입력
    nYear      IN NUMBER,  -- [IN] 해당 연도 입력
    nSemester  IN NUMBER  -- [IN] 해당 학기 입력
)
    IS
    CURSOR timetable_cur IS
        SELECT se.se_time AS 교시, -- 수업 시간 (예: 월1,화2 등 콤마구분)
               c.C_ID_NO AS 과목번호, -- 과목코드
               c.C_ID AS 과목명, -- 과목명
               se.se_section AS 분반, -- 분반번호
               c.c_unit AS 학점, -- 학점
               se.se_classroom AS 장소 --강의실위치
        FROM enroll e
                 JOIN sections se ON e.e_section = se.se_section  -- 분반기준섹션연결
                 JOIN courses c ON se.c_id_no = c.c_id_no --과목코드 기준 과목테이블연결
        WHERE e.s_id = sStudentId --해당학생 수강목록
          AND se.se_year = nYear --연도 필터
          AND se.se_semester = nSemester; --학기필터

    -- 커서에서 가져온 값들을 저장할 변수들
    vTime       VARCHAR2(100);          -- 수업시간
    vCourseId   VARCHAR2(20);           -- 과목코드
    vCourseName VARCHAR2(100);          -- 과목명
    vSection    VARCHAR2(10);           -- 분반번호
    vCredits    NUMBER;                 -- 학점
    vClassroom  VARCHAR2(50);           -- 강의실위치

    -- 합계용 변수
    vTotalSubjects NUMBER := 0;         -- 총 수강 과목 수
    vTotalCredits  NUMBER := 0;         -- 총 수강 학점 수
BEGIN
    -- 출력 헤더
    DBMS_OUTPUT.PUT_LINE('<< 수강신청 시간표 >>');
    DBMS_OUTPUT.PUT_LINE('교시 | 과목번호 | 과목명 | 분반 | 학점 | 장소');
    DBMS_OUTPUT.PUT_LINE('---------------------------------------------------------');

    -- 커서 열기
    OPEN timetable_cur;
    LOOP
        -- 커서에서 한 행씩 꺼내서 변수에 저장
        FETCH timetable_cur INTO vTime, vCourseId, vCourseName, vSection, vCredits, vClassroom;
        EXIT WHEN timetable_cur%NOTFOUND;

        -- 한 과목 시간표 출력
        DBMS_OUTPUT.PUT_LINE(
                vTime || ' | ' || vCourseId || ' | ' || vCourseName || ' | ' ||
                vSection || ' | ' || vCredits || ' | ' || vClassroom
        );
        -- 합계 계산
        vTotalSubjects := vTotalSubjects + 1;
        vTotalCredits := vTotalCredits + vCredits;
    END LOOP;

    -- 커서 닫기
    CLOSE timetable_cur;

    -- 총합 출력
    DBMS_OUTPUT.PUT_LINE('---------------------------------------------------------');
    DBMS_OUTPUT.PUT_LINE('총 신청 과목 수: ' || vTotalSubjects);
    DBMS_OUTPUT.PUT_LINE('총 신청 학점 수: ' || vTotalCredits);
END;
/