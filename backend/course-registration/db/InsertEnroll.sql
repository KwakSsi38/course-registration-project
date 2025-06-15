CREATE OR REPLACE PROCEDURE InsertEnroll (
    sStudentId IN VARCHAR2,   -- [IN] 수강신청 학생학번
    nSectionId IN NUMBER,     -- [IN] 신청할 섹션 ID (sections 테이블의 PK)
    result OUT VARCHAR2       -- [OUT] 결과 메시지 반환
)
    IS
    -- 사용자 정의 예외 선언
    too_many_sumCourseUnit  EXCEPTION;
    too_many_courses        EXCEPTION;
    too_many_students       EXCEPTION;
    duplicate_time          EXCEPTION;

    -- 변수 선언
    vSectionRow     sections%ROWTYPE;
    nCourseUnit     courses.c_unit%TYPE;
    nSumCourseUnit  NUMBER;
    nCnt            NUMBER;
    nYear           NUMBER;
    nSemester       NUMBER;
    nLeftSeats      NUMBER; -- ✅ 여석 계산용
BEGIN
    result := '';

    DBMS_OUTPUT.put_line('#');
    DBMS_OUTPUT.put_line(sStudentId || '님이 section_id=' || nSectionId || ' 수강 신청등록 요청');

    -- 1. 연도/학기 계산
    nYear := Date2EnrollYear(TO_DATE('2025-03-01', 'YYYY-MM-DD'));
    nSemester := Date2EnrollSemester(TO_DATE('2025-03-01', 'YYYY-MM-DD'));

    -- 2. 섹션 정보 조회
    SELECT * INTO vSectionRow
    FROM sections
    WHERE id = nSectionId;

    -- 2-1. 과목 학점 조회
    SELECT c_unit INTO nCourseUnit
    FROM courses
    WHERE c_id_no = vSectionRow.c_id_no;

    -- 2-2. 섹션 유효성 검사
    SELECT COUNT(*) INTO nCnt
    FROM sections
    WHERE id = nSectionId
      AND c_id_no = vSectionRow.c_id_no
      AND se_year = nYear
      AND se_semester = nSemester
      AND se_section = vSectionRow.se_section;

    IF nCnt = 0 THEN
        result := '해당 섹션 정보가 올바르지 않습니다';
        RETURN;
    END IF;

    -- 3. 학기 총 학점
    SELECT NVL(SUM(c.c_unit), 0) INTO nSumCourseUnit
    FROM enroll e
             JOIN sections s ON e.e_section = s.id
             JOIN courses c ON s.c_id_no = c.c_id_no
    WHERE e.s_id = sStudentId
      AND s.se_year = nYear
      AND s.se_semester = nSemester;

    IF nSumCourseUnit + nCourseUnit > 18 THEN
        RAISE too_many_sumCourseUnit;
    END IF;

    -- 4. 중복 과목 확인
    SELECT COUNT(*) INTO nCnt
    FROM enroll e
             JOIN sections s ON e.e_section = s.id
    WHERE e.s_id = sStudentId
      AND s.se_year = nYear
      AND s.se_semester = nSemester
      AND s.c_id_no = vSectionRow.c_id_no;

    IF nCnt > 0 THEN
        RAISE too_many_courses;
    END IF;

    -- 5. 정원 초과 확인
    SELECT COUNT(*) INTO nCnt
    FROM enroll
    WHERE e_section = nSectionId;

    IF nCnt >= vSectionRow.se_capacity THEN
        RAISE too_many_students;
    END IF;

    -- 6. 시간 중복 확인
    SELECT COUNT(*) INTO nCnt
    FROM enroll e
             JOIN sections s ON e.e_section = s.id
    WHERE e.s_id = sStudentId
      AND s.se_year = nYear
      AND s.se_semester = nSemester
      AND EXISTS (
        SELECT 1 FROM (
                          SELECT REGEXP_SUBSTR(vSectionRow.se_time, '[^,]+', 1, LEVEL) AS one_time
                          FROM dual CONNECT BY LEVEL <= REGEXP_COUNT(vSectionRow.se_time, ',') + 1
                      )
        WHERE one_time IS NOT NULL
          AND INSTR(s.se_time, one_time) > 0
    );

    IF nCnt > 0 THEN
        RAISE duplicate_time;
    END IF;

    -- 7. 수강신청 등록
    INSERT INTO enroll(s_id, e_year, e_semester, c_id_no, e_section, c_id)
    VALUES (
               sStudentId,
               nYear,
               nSemester,
               vSectionRow.c_id_no,
               vSectionRow.se_section,
               vSectionRow.c_id
           );

    -- 8. 여석 계산
    SELECT COUNT(*) INTO nCnt
    FROM enroll
    WHERE e_section = nSectionId;

    nLeftSeats := vSectionRow.se_capacity - nCnt;

    COMMIT;

    -- ✅ 여석 포함 결과 메시지
    result := '완료. 여석' || nLeftSeats;

-- 예외 처리
EXCEPTION
    WHEN too_many_sumCourseUnit THEN
        result := '최대학점을 초과하였습니다';
    WHEN too_many_courses or DUP_VAL_ON_INDEX THEN
        result := '이미 같은 과목을 수강 신청하였습니다';
    WHEN too_many_students THEN
        result := '수강 정원이 초과되었습니다';
    WHEN duplicate_time THEN
        result := '이미 신청한 과목과 시간이 중복됩니다';
    WHEN OTHERS THEN
        ROLLBACK;
        result := '오류 발생: ' || SQLERRM;
END;
/


