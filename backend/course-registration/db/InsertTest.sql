Set ServerOutput On;
/

DECLARE
    result  VARCHAR2(50) := '';
BEGIN

    DBMS_OUTPUT.enable;

    DBMS_OUTPUT.put_line
    ('**************** Insert 및 에러 처리 테스트 ********************');

/* 에러 처리 2 : 동일한 과목 신청 여부 :데이터베이스 과목 신청 */
    InsertEnroll('20011234', 'C400', 3, result);
    DBMS_OUTPUT.put_line('결과 : ' || result);



/* 에러 처리 3 : 수강신청 인원 초과 여부 : 객체지향 윈도우즈 프로그래밍 신청*/
    InsertEnroll('20011234', 'C900', 3, result);
    DBMS_OUTPUT.put_line('결과 : ' || result);


/* 에러 처리 4 : 신청한 과목들 시간 중복 여부  : 멀티미디어 개론 신청 */
    InsertEnroll('20011234', 'M100', 3, result);
    DBMS_OUTPUT.put_line('결과 : ' || result);


/*  에러가 없는 경우  */
    InsertEnroll('20011234', 'C800', 3, result);
    DBMS_OUTPUT.put_line('결과 : ' || result);


/*  에러 처리 1 : 최대 학점 초과 여부 검사 : 게임 프로그래밍 신청 */
    InsertEnroll('20011234', 'M700', 3, result);
    DBMS_OUTPUT.put_line('결과  : ' || result);

    DBMS_OUTPUT.put_line
    ('***************** CURSOR를 이용한 SELECT 테스트 ****************');
/* 최종 결과 확인  */
    SelectTimeTable('20011234', 2004, 1);

    delete from enroll where s_id='20011234' and c_id='C800' and c_id_no=3;

END;
/
