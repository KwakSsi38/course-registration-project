--DROP VIEW "V_StudentTimetable";

CREATE OR REPLACE PROCEDURE SelectTimeTable (
    sStudentId IN VARCHAR2,
    nYear      IN NUMBER,
    nSemester  IN NUMBER
)
    IS
    CURSOR timetable_cur IS
        SELECT
            se.se_time AS 교시,
            c.c_id_no AS 과목번호,
            c.c_id AS 과목명,
            se.se_section AS 분반,
            c.c_unit AS 학점,
            se.se_classroom AS 장소
        FROM enroll e
                 JOIN sections se ON e.c_id_no = se.c_id_no
            AND e.e_section = se.se_section
            AND e.e_year = se.se_year
            AND e.e_semester = se.se_semester
                 JOIN courses c ON se.c_id_no = c.c_id_no
        WHERE e.s_id = sStudentId
          AND se.se_year = nYear
          AND se.se_semester = nSemester;

    vTime        VARCHAR2(100);
    vCourseId    VARCHAR2(20);
    vCourseName  VARCHAR2(100);
    vSection     VARCHAR2(10);
    vCredits     NUMBER;
    vClassroom   VARCHAR2(50);
    vTotalSubjects NUMBER := 0;
    vTotalCredits  NUMBER := 0;
BEGIN
    DBMS_OUTPUT.PUT_LINE('<< 수강신청 시간표 >>');
    DBMS_OUTPUT.PUT_LINE('교시 | 과목번호 | 과목명 | 분반 | 학점 | 장소');
    DBMS_OUTPUT.PUT_LINE('---------------------------------------------------------');

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

    DBMS_OUTPUT.PUT_LINE('---------------------------------------------------------');
    DBMS_OUTPUT.PUT_LINE('총 신청 과목 수: ' || vTotalSubjects);
    DBMS_OUTPUT.PUT_LINE('총 신청 학점 수: ' || vTotalCredits);
END;
/