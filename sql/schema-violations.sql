
USE university_db;

-- Test 1: Insert a NULL student email
INSERT INTO student (student_code, full_name, email)
VALUES ('S901', 'Test Student', NULL);

-- Test 2: Insert a duplicate student email
INSERT INTO student (student_code, full_name, email)
VALUES ('S902', 'Duplicate Student', 'kamal@example.com');

-- Test 3: Insert an invalid credit value
INSERT INTO course (code, title, credits, lecturer_id)
VALUES ('CS998', 'Invalid Course', 10, 1);

-- Test 4: Insert a course with an invalid lecturer ID
INSERT INTO course (code, title, credits, lecturer_id)
VALUES ('CS999', 'Invalid Lecturer Course', 3, 9999);

-- Test 5: Delete a department referenced by lecturers
DELETE FROM department
WHERE id = 1;
