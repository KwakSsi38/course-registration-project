CREATE OR REPLACE VIEW V_StudentTimetable AS
SELECT
    e.s_id AS student_id,
    se.se_time AS time,
    c.C_ID_NO AS course_id,
    c.C_ID AS course_name,
    se.se_section AS section,
    c.c_unit AS credit,
    se.se_classroom AS classroom
FROM enroll e
         JOIN sections se ON e.e_section = se.id
         JOIN courses c ON se.c_id_no = c.c_id_no;