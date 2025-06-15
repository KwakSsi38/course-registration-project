CREATE OR REPLACE PROCEDURE InsertEnroll (
    sStudentId      IN  VARCHAR2,      -- [IN] 수강신청 학생학번
    nSectionId      IN  NUMBER,        -- [IN] 신청할 섹션 ID (sections 테이블의 PK)
    result          OUT VARCHAR2,      -- [OUT] 결과 메시지 반환
    nLeftSeats      OUT NUMBER         -- [OUT] 남은 여석 반환
)
IS
    -- 사용자 정의 예외 선언
    too_many_sumCourseUnit  EXCEPTION;
    too_many_courses        EXCEPTION;
    too_many_students       EXCEPTION;
    duplicate_time          EXCEPTION;
    section_not_found       EXCEPTION; -- 추가: 섹션이 존재하지 않을 경우

    -- 변수 선언
    vSectionRow     sections%ROWTYPE;
    nCourseUnit     courses.c_unit%TYPE;
    nSumCourseUnit  NUMBER;
    nEnrolledCount  NUMBER; -- 현재 섹션에 수강 신청된 인원 수
    nYear           NUMBER;
    nSemester       NUMBER;
BEGIN
    result := '';
    nLeftSeats := 0; -- OUT 파라미터 초기화 (기본값)
    
    DBMS_OUTPUT.put_line('#');
    DBMS_OUTPUT.put_line(sStudentId || '님이 section_id=' || nSectionId || ' 수강 신청 요청');

    -- 1. 연도/학기 계산 (Date2EnrollYear, Date2EnrollSemester 함수가 있다고 가정)
    nYear := Date2EnrollYear(TO_DATE('2025-03-01', 'YYYY-MM-DD'));
    nSemester := Date2EnrollSemester(TO_DATE('2025-03-01', 'YYYY-MM-DD'));

    -- 2. 섹션 정보 조회
    BEGIN
        SELECT * INTO vSectionRow
        FROM sections
        WHERE id = nSectionId
          AND se_year = nYear
          AND se_semester = nSemester; -- 현재 연도/학기 조건 추가
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE section_not_found; -- 해당 섹션이 없으면 예외 발생
    END;

    -- 2-1. 과목 학점 조회
    SELECT c_unit INTO nCourseUnit
    FROM courses
    WHERE c_id_no = vSectionRow.c_id_no;

    -- 3. 학기 총 학점 확인 (총 학점이 18학점을 초과하는지)
    -- Enroll 테이블과 Courses 테이블을 e.c_id_no로 조인
    SELECT NVL(SUM(CO.c_unit), 0) INTO nSumCourseUnit
    FROM enroll E
    JOIN courses CO ON E.c_id_no = CO.c_id_no -- Enroll의 c_id_no와 Courses의 c_id_no 조인
    WHERE E.s_id = sStudentId
      AND E.e_year = nYear
      AND E.e_semester = nSemester;

    IF nSumCourseUnit + nCourseUnit > 18 THEN
        RAISE too_many_sumCourseUnit;
    END IF;

    -- 4. 중복 과목 확인 (동일한 과목번호로 이미 신청했는지)
    SELECT COUNT(*) INTO nEnrolledCount
    FROM enroll
    WHERE s_id = sStudentId
      AND e_year = nYear
      AND e_semester = nSemester
      AND c_id_no = vSectionRow.c_id_no; -- Enroll의 c_id_no로 확인 (vSectionRow.c_id_no는 Courses의 c_id_no와 동일)

    IF nEnrolledCount > 0 THEN
        RAISE too_many_courses;
    END IF;

    -- 5. 정원 초과 확인 (현재 신청 인원 vs. 정원)
    SELECT COUNT(*) INTO nEnrolledCount
    FROM enroll E
    WHERE E.c_id_no = vSectionRow.c_id_no
      AND E.e_year = vSectionRow.se_year
      AND E.e_semester = vSectionRow.se_semester
      AND E.e_section = vSectionRow.se_section;

    IF nEnrolledCount >= vSectionRow.se_capacity THEN
        RAISE too_many_students;
    END IF;

    -- 6. 시간 중복 확인 (신청하려는 과목의 시간과 이미 신청한 과목의 시간 중복)
    SELECT COUNT(*) INTO nEnrolledCount
    FROM enroll E_EXIST
    -- Enroll 테이블에서 c_id_no, e_year, e_semester, e_section을 통해 Sections 정보와 조인
    JOIN sections S_EXIST ON E_EXIST.c_id_no = S_EXIST.c_id_no
                         AND E_EXIST.e_year = S_EXIST.se_year
                         AND E_EXIST.e_semester = S_EXIST.se_semester
                         AND E_EXIST.e_section = S_EXIST.se_section
    WHERE E_EXIST.s_id = sStudentId
      AND E_EXIST.e_year = nYear
      AND E_EXIST.e_semester = nSemester
      AND EXISTS (
        -- 신청하려는 과목의 시간을 파싱하여 이미 신청된 과목의 시간과 겹치는지 확인
        SELECT 1 FROM (
            SELECT REGEXP_SUBSTR(vSectionRow.se_time, '[^,]+', 1, LEVEL) AS one_time_new
            FROM dual CONNECT BY LEVEL <= REGEXP_COUNT(vSectionRow.se_time, ',') + 1
        ) NEW_TIMES
        WHERE NEW_TIMES.one_time_new IS NOT NULL
          AND INSTR(S_EXIST.se_time, NEW_TIMES.one_time_new) > 0
    );

    IF nEnrolledCount > 0 THEN
        RAISE duplicate_time;
    END IF;

    -- 7. 모든 유효성 검사 통과 시, 수강신청 등록
    INSERT INTO enroll(s_id, e_year, e_semester, c_id_no, e_section, c_id)
    VALUES (
        sStudentId,
        nYear,
        nSemester,
        vSectionRow.c_id_no,
        vSectionRow.se_section,
        vSectionRow.c_id -- c_id는 과목명을 의미한다고 가정
    );

    -- 8. 등록 후 최종 여석 계산
    -- INSERT 작업이 완료된 후 실제 DB에 반영된 인원 수를 다시 카운트
    SELECT COUNT(*) INTO nEnrolledCount
    FROM enroll E
    WHERE E.c_id_no = vSectionRow.c_id_no
      AND E.e_year = vSectionRow.se_year
      AND E.e_semester = vSectionRow.se_semester
      AND E.e_section = vSectionRow.se_section;

    nLeftSeats := vSectionRow.se_capacity - nEnrolledCount;

    COMMIT; -- 모든 작업 성공 시 커밋

    result := '수강 신청 완료. 남은 여석: ' || nLeftSeats || '명';

-- 예외 처리
EXCEPTION
    WHEN section_not_found THEN
        ROLLBACK; -- 변경사항 롤백
        result := '해당 섹션 정보를 찾을 수 없습니다.';
        nLeftSeats := 0; -- 유효하지 않은 섹션이므로 여석 0 (또는 -1)
    WHEN too_many_sumCourseUnit THEN
        ROLLBACK;
        result := '최대학점을 초과하였습니다';
        -- 이 시점의 nEnrolledCount는 삽입 전의 값 (정원 초과 검사 시 사용된 값)
        -- 프로시저 시작 시점의 여석을 반환하거나 특정 값으로 설정
        nLeftSeats := vSectionRow.se_capacity - nEnrolledCount;
    WHEN too_many_courses THEN
        ROLLBACK;
        result := '이미 같은 과목을 수강 신청하였습니다';
        nLeftSeats := vSectionRow.se_capacity - nEnrolledCount;
    WHEN too_many_students THEN
        ROLLBACK;
        result := '수강 정원이 초과되었습니다';
        nLeftSeats := vSectionRow.se_capacity - nEnrolledCount;
    WHEN duplicate_time THEN
        ROLLBACK;
        result := '이미 신청한 과목과 시간이 중복됩니다';
        nLeftSeats := vSectionRow.se_capacity - nEnrolledCount;
    WHEN DUP_VAL_ON_INDEX THEN -- UNIQUE 제약 조건 위반 (예: 동일 과목 재신청 시)
        ROLLBACK;
        result := '이미 같은 과목을 수강 신청하였습니다';
        nLeftSeats := vSectionRow.se_capacity - nEnrolledCount;
    WHEN OTHERS THEN -- 예상치 못한 모든 오류에 대해
        ROLLBACK; -- 변경사항 롤백
        result := '수강 신청 중 오류 발생: ' || SQLERRM; -- SQL 오류 메시지 포함
        nLeftSeats := -1; -- 오류 시 여석을 -1로 설정하여 오류 상태를 명확히 알림
END;
/