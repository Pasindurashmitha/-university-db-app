
USE university_db;

-- Join all five normalized tables
SELECT
    s.student_code,
    s.full_name AS student_name,
    c.code AS course_code,
    c.title AS course_title,
    c.credits,
    e.grade,
    l.name AS lecturer_name,
    l.email AS lecturer_email,
    d.name AS department_name
FROM enrollment e
INNER JOIN student s
    ON e.student_id = s.id
INNER JOIN course c
    ON e.course_id = c.id
INNER JOIN lecturer l
    ON c.lecturer_id = l.id
INNER JOIN department d
    ON l.department_id = d.id
ORDER BY s.student_code, c.code;

