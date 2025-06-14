SELECT
    s.id AS section_id,            -- 섹션 고유 ID (기본키)
    c.c_id_no AS course_code,      -- 과목번호
    c.c_id AS course_name,         -- 과목명
    c.c_unit AS credit,            -- 학점
    c.c_major AS major,            -- 전공
    s.se_section AS section_no,    -- 분반 번호
    s.se_time AS class_time,       -- 수업 시간 (예: 월1, 수3)
    s.se_capacity AS capacity,     -- 수강 정원
    s.se_classroom AS classroom    -- 강의실 위치
FROM
    sections s
        JOIN
    courses c ON s.c_id_no = c.c_id_no
ORDER BY
    c.c_id_no, s.se_section;