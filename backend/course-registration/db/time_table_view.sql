CREATE
OR REPLACE VIEW V_StudentTimetable AS
SELECT
  e.s_id AS student_id,
  e.e_year AS year,
  e.e_semester AS semester,
  c.c_id_no AS courseIdNo,
  c.c_id AS courseName,
  e.e_section AS section,
  c.c_unit AS credit,
  c.c_major AS major,
  p.p_name AS professorName,
  se.se_time AS time,
  se.se_classroom AS classroom
FROM
  Enroll e
  JOIN Courses c ON e.c_id_no = c.c_id_no
  JOIN Sections se ON se.c_id_no = e.c_id_no
  AND se.se_section = e.e_section
  AND se.se_year = e.e_year
  AND se.se_semester = e.e_semester
  JOIN Professors p ON se.p_id = p.p_id;