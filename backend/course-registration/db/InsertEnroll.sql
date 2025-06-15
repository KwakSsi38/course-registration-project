CREATE OR REPLACE PROCEDURE InsertEnroll (
    sStudentId   IN  VARCHAR2,   -- [IN] 수강신청 학생 학번
    nSectionId   IN  NUMBER,     -- [IN] 신청할 섹션의 ID (sections 테이블 PK)
    result       OUT VARCHAR2,  -- [OUT] 결과 메시지
    nLeftSeats   OUT NUMBER     -- [OUT] 남은 여석 수
)
    IS
    -- 🔸 사용자 정의 예외 선언
    too_many_sumCourseUnit  EXCEPTION;  -- 최대 학점 초과
    too_many_courses        EXCEPTION;  -- 동일 과목 중복 신청
    too_many_students       EXCEPTION;  -- 수강 정원 초과
    duplicate_time          EXCEPTION;  -- 시간 중복

-- 🔸 내부 변수 선언
    vSectionRow     sections%ROWTYPE;           -- 섹션 전체 정보
    nCourseUnit     courses.c_unit%TYPE;        -- 현재 신청 과목의 학점
    nSumCourseUnit  NUMBER := 0;                -- 현재 학기의 총 신청 학점
    nCnt            NUMBER := 0;                -- 조건 일치 건수 체크용
    nYear           NUMBER;                     -- 현재 연도
    nSemester       NUMBER;                     -- 현재 학기
BEGIN
    result := '';

    -- 🔸 수강신청 로그 출력
    DBMS_OUTPUT.put_line('#');
    DBMS_OUTPUT.put_line(sStudentId || '님이 section_id=' || nSectionId || ' 수강 신청 요청');

    -- 1. 현재 연도 및 학기 계산 (임시 고정 날짜로)
    nYear := Date2EnrollYear(TO_DATE('2025-03-01', 'YYYY-MM-DD'));
    nSemester := Date2EnrollSemester(TO_DATE('2025-03-01', 'YYYY-MM-DD'));

    -- 2. 신청할 섹션 정보 조회
SELECT * INTO vSectionRow
FROM sections
WHERE id = nSectionId;

-- 3. 과목 학점 조회
SELECT c_unit INTO nCourseUnit
FROM courses
WHERE c_id_no = vSectionRow.c_id_no;

DBMS_OUTPUT.put_line('이번 과목 학점: ' || nCourseUnit);

    -- 4. 현재 학기의 총 신청 학점 조회
SELECT NVL(SUM(c.c_unit), 0) INTO nSumCourseUnit
FROM enroll e
         JOIN sections s ON e.e_section = s.se_section
    AND s.se_year = nYear
    AND s.se_semester = nSemester
    AND s.c_id_no = e.c_id_no
         JOIN courses c ON s.c_id_no = c.c_id_no
WHERE e.s_id = sStudentId;

DBMS_OUTPUT.put_line('기존 신청 학점: ' || nSumCourseUnit);

    -- 4-1. 최대 18학점 초과 여부 체크
    IF nSumCourseUnit + nCourseUnit > 18 THEN
        RAISE too_many_sumCourseUnit;
END IF;

    -- 5. 동일 과목 중복 신청 여부 확인
SELECT COUNT(*) INTO nCnt
FROM enroll e
         JOIN sections s ON e.e_section = s.se_section
    AND s.se_year = nYear
    AND s.se_semester = nSemester
WHERE e.s_id = sStudentId
  AND s.c_id_no = vSectionRow.c_id_no;

IF nCnt > 0 THEN
        RAISE too_many_courses;
END IF;

    -- 6. 정원 초과 여부 확인
SELECT COUNT(*) INTO nCnt
FROM enroll e
WHERE e.e_year = vSectionRow.se_year
  AND e.e_semester = vSectionRow.se_semester
  AND e.c_id_no = vSectionRow.c_id_no
  AND e.e_section = vSectionRow.se_section;

IF nCnt >= vSectionRow.se_capacity THEN
        RAISE too_many_students;
END IF;

    -- 7. 시간 중복 여부 확인 (한 과목 시간 단위로 분리하여 체크)
SELECT COUNT(*) INTO nCnt
FROM enroll e
         JOIN sections s ON e.e_section = s.se_section
    AND s.se_year = nYear
    AND s.se_semester = nSemester
    AND s.c_id_no = e.c_id_no
WHERE e.s_id = sStudentId
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

    -- 8. 수강신청 등록
INSERT INTO enroll(s_id, e_year, e_semester, c_id_no, e_section, c_id)
VALUES (
           sStudentId,
           nYear,
           nSemester,
           vSectionRow.c_id_no,
           vSectionRow.se_section,
           vSectionRow.c_id
       );

-- 9. 여석 계산 = 총 정원 - 현재 신청 인원
SELECT COUNT(*) INTO nCnt
FROM enroll
WHERE e_year = vSectionRow.se_year
  AND e_semester = vSectionRow.se_semester
  AND c_id_no = vSectionRow.c_id_no
  AND e_section = vSectionRow.se_section;

nLeftSeats := vSectionRow.se_capacity - nCnt;

    -- 10. 커밋 및 결과 메시지 설정
COMMIT;
result := '수강신청 등록이 완료되었습니다. (남은 여석: ' || nLeftSeats || '명)';

-- 🔸 예외 처리 블록
EXCEPTION
    WHEN too_many_sumCourseUnit THEN
        result := '최대학점을 초과하였습니다';
WHEN too_many_courses THEN
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


