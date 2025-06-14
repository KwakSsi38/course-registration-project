CREATE OR REPLACE PROCEDURE DeleteEnroll (
    sStudentId   IN VARCHAR2,   -- [IN] 삭제할 학생 ID
    vCourseIdNo  IN VARCHAR2,   -- [IN] 삭제할 과목 번호 (courses.c_id_no)
    vSectionNo   IN VARCHAR2,   -- [IN] 삭제할 분반 번호 (sections.se_section)
    result       OUT VARCHAR2   -- [OUT] 결과 메시지
)
    IS
    nYear      NUMBER;   -- 현재 연도
    nSemester  NUMBER;   -- 현재 학기
    nCnt       NUMBER;   -- 삭제된 행 수 확인용 변수
BEGIN
    -- 1. 현재 연도 및 학기 추출
    nYear := Date2EnrollYear(SYSDATE);
    nSemester := Date2EnrollSemester(SYSDATE);

    -- 2. 삭제 실행
    DELETE FROM enroll
    WHERE s_id = sStudentId
      AND e_year = nYear
      AND e_semester = nSemester
      AND c_id_no = vCourseIdNo
      AND e_section = vSectionNo;

    -- 3. 삭제 여부 확인
    nCnt := SQL%ROWCOUNT;

    IF nCnt = 0 THEN
        result := '해당 과목에 대한 수강 신청 내역이 존재하지 않습니다.';
    ELSE
        COMMIT;
        result := '수강 신청 삭제가 완료되었습니다.';
    END IF;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        result := '오류 발생: ' || SQLERRM;
END;
/