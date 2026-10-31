
USE university_db;

-- Demonstrate update anomaly
UPDATE registration_flat
SET lecturer_email = 'newperera@uni.lk'
WHERE student_id = 'S001'
  AND course_code = 'CS101';

-- Check whether the same lecturer email
-- is stored differently in other records
SELECT *
FROM registration_flat
WHERE lecturer_name = 'Dr. Perera';

-- Demonstrate deletion anomaly
-- Delete the student S004 record
DELETE FROM registration_flat
WHERE student_id = 'S004';
