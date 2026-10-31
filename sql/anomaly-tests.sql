
USE university_db;

-- Check for duplicate student details
SELECT student_code, COUNT(*) AS record_count
FROM student
GROUP BY student_code
HAVING COUNT(*) > 1;

-- Check for duplicate course details
SELECT code, COUNT(*) AS record_count
FROM course
GROUP BY code
HAVING COUNT(*) > 1;

-- Check for duplicate student-course enrollments
SELECT student_id, course_id, COUNT(*) AS record_count
FROM enrollment
GROUP BY student_id, course_id
HAVING COUNT(*) > 1;

-- Check enrollment records without valid students
SELECT e.*
FROM enrollment e
LEFT JOIN student s ON e.student_id = s.id
WHERE s.id IS NULL;

-- Check enrollment records without valid courses
SELECT e.*
FROM enrollment e
LEFT JOIN course c ON e.course_id = c.id
WHERE c.id IS NULL;
