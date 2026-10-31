package com.example.university_db_app.repository;

import com.example.university_db_app.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    // SELECT ... FROM student WHERE student_code = ?
    Optional<Student> findByStudentCode(String studentCode);

    // WHERE LOWER(full_name) LIKE LOWER('%?%')
    List<Student> findByFullNameContainingIgnoreCase(String text);

    // ORDER BY full_name ASC
    List<Student> findAllByOrderByFullNameAsc();

    // WHERE email = ? (returns true / false)
    boolean existsByEmail(String email);

    // WHERE email LIKE '%?' (count rows)
    long countByEmailEndingWith(String domain);
}