DROP PROCEDURE InsertEnroll;




CREATE OR REPLACE PROCEDURE InsertEnroll (
    sStudentId IN VARCHAR2,   -- [IN] 수강신청 학생학번
    nSectionId IN NUMBER,     -- [IN] 신청할 섹션 ID (sections 테이블의 PK)
    result OUT VARCHAR2       -- [OUT] 결과 메시지 반환
)
    IS
    -- 사용자 정의 예외 선언
    too_many_sumCourseUnit  EXCEPTION;  -- 최대학점 초과 예외처리
    too_many_courses        EXCEPTION;  -- 중복 과목 예외처리
    too_many_students       EXCEPTION;  -- 수강 정원 초과 예외처리
    duplicate_time          EXCEPTION;  -- 시간 중복 예외처리

-- 변수 선언
    vSectionRow     sections%ROWTYPE;          -- 섹션 정보
    nCourseUnit     courses.c_unit%TYPE;       -- 신청 과목 학점
    nSumCourseUnit  NUMBER;                    -- 현재까지 신청한 총 학점
    nCnt            NUMBER;                    -- 임시 카운트 변수
    nYear           NUMBER;                    -- 현재 연도
    nSemester       NUMBER;                    -- 현재 학기
    vStudentName    students.s_name%TYPE;      -- 학생 이름

BEGIN
    result := ''; -- 결과 초기화

    DBMS_OUTPUT.put_line('#');
    DBMS_OUTPUT.put_line(sStudentId || '님이 section_id=' || nSectionId || ' 수강 신청등록 요청');

    -- 1. 현재 연도 및 학기 계산
    nYear := Date2EnrollYear(TO_DATE('2025-03-01', 'YYYY-MM-DD')); --날짜 SYSDATE하면 2학기라 지정
    nSemester := Date2EnrollSemester(TO_DATE('2025-03-01', 'YYYY-MM-DD')); --날짜 SYSDATE하면 2학기라 지정

    -- 2. 섹션 정보 조회 (현재 연도/학기에 존재하는지 확인)
    SELECT * INTO vSectionRow
    FROM sections
    WHERE id = nSectionId
      AND se_year = nYear
      AND se_semester = nSemester;

    -- 3. 과목 정보 및 학생 이름 조회
    SELECT c_unit INTO nCourseUnit
    FROM courses
    WHERE c_id_no = vSectionRow.c_id_no;

    SELECT s_name INTO vStudentName
    FROM students
    WHERE s_id = sStudentId;

    -- 4. 수강신청 상세 출력
    DBMS_OUTPUT.put_line('--- 수강신청 요청 정보 ---');
    DBMS_OUTPUT.put_line('학번     : ' || sStudentId);
    DBMS_OUTPUT.put_line('이름     : ' || vStudentName);
    DBMS_OUTPUT.put_line('연도     : ' || vSectionRow.se_year);
    DBMS_OUTPUT.put_line('학기     : ' || vSectionRow.se_semester);
    DBMS_OUTPUT.put_line('과목번호 : ' || vSectionRow.c_id_no);
    DBMS_OUTPUT.put_line('과목명   : ' || vSectionRow.c_id);
    DBMS_OUTPUT.put_line('분반     : ' || vSectionRow.se_section);

    -- 5. 최대 학점 초과 확인
    SELECT NVL(SUM(c.c_unit), 0)
    INTO nSumCourseUnit
    FROM enroll e
             JOIN sections s ON e.e_section = s.id
             JOIN courses c ON s.c_id_no = c.c_id_no
    WHERE e.s_id = sStudentId
      AND s.se_year = nYear
      AND s.se_semester = nSemester;

    IF nSumCourseUnit + nCourseUnit > 18 THEN
        RAISE too_many_sumCourseUnit;
    END IF;

    -- 6. 동일 과목 중복 신청 확인
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

    -- 7. 정원 초과 확인
    SELECT COUNT(*) INTO nCnt
    FROM enroll
    WHERE e_section = nSectionId;

    IF nCnt >= vSectionRow.se_capacity THEN
        RAISE too_many_students;
    END IF;

    -- 8. 시간 중복 확인
    SELECT COUNT(*) INTO nCnt
    FROM enroll e
             JOIN sections s ON e.e_section = s.id
    WHERE e.s_id = sStudentId
      AND s.se_year = nYear
      AND s.se_semester = nSemester
      AND EXISTS (
        SELECT 1
        FROM (
                 SELECT REGEXP_SUBSTR(vSectionRow.se_time, '[^,]+', 1, LEVEL) AS one_time
                 FROM dual
                 CONNECT BY LEVEL <= REGEXP_COUNT(vSectionRow.se_time, ',') + 1
             )
        WHERE one_time IS NOT NULL
          AND INSTR(s.se_time, one_time) > 0
    );

    IF nCnt > 0 THEN
        RAISE duplicate_time;
    END IF;

    -- 9. INSERT 수행
    INSERT INTO enroll(s_id, e_year, e_semester, c_id_no, e_section, c_id)
    VALUES (
               sStudentId,
               nYear,
               nSemester,
               vSectionRow.c_id_no,
               vSectionRow.se_section,
               vSectionRow.c_id
           );

    COMMIT;
    result := '수강신청 등록이 완료되었습니다.';

-- 10. 예외 처리
EXCEPTION
    WHEN too_many_sumCourseUnit THEN
        result := '최대학점을 초과하였습니다';

    WHEN too_many_courses THEN
        result := '이미 같은 과목을 수강 신청하였습니다';

    WHEN too_many_students THEN
        result := '수강 정원이 초과되었습니다';

    WHEN duplicate_time THEN
        result := '이미 신청한 과목과 시간이 중복됩니다';

    WHEN NO_DATA_FOUND THEN
        result := '해당 섹션은 현재 학기에 존재하지 않습니다';

    WHEN OTHERS THEN
        IF SQLCODE = -1 THEN  -- ORA-00001: 중복 INSERT
            result := '이미 같은 과목을 수강 신청하였습니다';
        ELSE
            ROLLBACK;
            result := '오류 발생: ' || SQLERRM;
        END IF;
END;
/