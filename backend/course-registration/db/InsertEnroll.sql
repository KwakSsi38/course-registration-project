CREATE OR REPLACE PROCEDURE InsertEnroll (
    sStudentId IN VARCHAR2,   -- [IN] 수강신청 학생학번
    nSectionId IN NUMBER,     -- [IN] 신청할 섹션 ID (sections 테이블의 PK)
    result OUT VARCHAR2       -- [OUT] 결과 메시지 반환
)
IS
    -- 사용자 정의 예외 선언
    too_many_sumCourseUnit  EXCEPTION;  -- 최대학점초과 예외처리
    too_many_courses        EXCEPTION;  -- 중복 과목 예외처리
    too_many_students       EXCEPTION;  -- 수강신청 인원 초과 예외처리
    duplicate_time          EXCEPTION;  -- 신청한 과목들 시간 중복 여부 예외처리

    -- %ROWTYPE / %TYPE 적용된 변수 선언
    vSectionRow     sections%ROWTYPE;          -- 섹션 레코드 전체 조회용
    nCourseUnit     courses.c_unit%TYPE;       -- 과목 학점
    nSumCourseUnit  NUMBER;                    -- 현재까지 수강한 총 학점
    nCnt            NUMBER;                    -- 카운트 임시변수
    nYear           NUMBER;                    -- 현재 연도 추출
    nSemester       NUMBER;                    -- 현재 학기 추출
BEGIN
    result := ''; -- 결과 초기화

    -- 디버깅용 출력
    DBMS_OUTPUT.put_line('#');
    DBMS_OUTPUT.put_line(sStudentId || '님이 section_id=' || nSectionId || ' 수강 신청등록 요청');

    -- 1. 현재날짜 연도/학기 추출
    nYear := Date2EnrollYear(SYSDATE);
    nSemester := Date2EnrollSemester(SYSDATE);

    -- 2. section_id로 섹션 레코드 통째로 조회
    SELECT * INTO vSectionRow
    FROM sections
    WHERE id = nSectionId;

    -- 2-1. 과목 학점 조회 (courses 테이블 참조)
    SELECT c_unit INTO nCourseUnit
    FROM courses
    WHERE c_id_no = vSectionRow.c_id_no;

    /* 에러 처리 1 : 최대학점 초과여부 */
    -- 3. 이미 수강 중인 학기의 학점 합산 조회
    SELECT NVL(SUM(c.c_unit), 0)
    INTO nSumCourseUnit
    FROM enroll e
         JOIN sections s ON e.e_section = s.id
         JOIN courses c ON s.c_id_no = c.c_id_no
    WHERE e.s_id = sStudentId
      AND s.se_year = nYear
      AND s.se_semester = nSemester;

    -- 3-1. 최대 학점(18학점) 초과 여부 검사
    IF nSumCourseUnit + nCourseUnit > 18 THEN
        RAISE too_many_sumCourseUnit;
    END IF;

    -- 4. 동일한 과목번호로 이미 수강 신청했는지 검사
    SELECT COUNT(*)
    INTO nCnt
    FROM enroll e
         JOIN sections s ON e.e_section = s.id
    WHERE e.s_id = sStudentId
      AND s.se_year = nYear
      AND s.se_semester = nSemester
      AND s.c_id_no = vSectionRow.c_id_no;

    IF nCnt > 0 THEN
        RAISE too_many_courses;
    END IF;

    -- 5. 해당 섹션의 현재 수강 인원 조회
    SELECT COUNT(*)
    INTO nCnt
    FROM enroll
    WHERE e_section = nSectionId;

    -- 5-1. 수강 정원 초과 여부 확인
    IF nCnt >= vSectionRow.se_capacity THEN
        RAISE too_many_students;
    END IF;

    -- 6. 시간 중복 확인
    SELECT COUNT(*)
    INTO nCnt
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

    -- 7. 수강 신청 등록
    INSERT INTO enroll(s_id, e_year, e_semester, c_id_no, e_section, c_id)
    VALUES (
        sStudentId,
        nYear,
        nSemester,
        vSectionRow.c_id_no,
        vSectionRow.se_section,
        vSectionRow.c_id
    );

    -- 8. 커밋
    COMMIT;
    result := '수강신청 등록이 완료되었습니다.';

-- 예외 처리 블록
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