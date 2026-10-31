package com.example.university_db_app.repository;

import com.example.university_db_app.entity.Enrollment;
import com.example.university_db_app.dto.CourseEnrollmentCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudent_StudentCode(String studentCode);

    List<Enrollment> findByCourse_Code(String courseCode);

    List<Enrollment> findByGrade(String grade);

    // WHERE grade IN (?, ?, ...)
    List<Enrollment> findByGradeIn(Collection<String> grades);

    // Find enrollments by student code and grade
    List<Enrollment> findByStudent_StudentCodeAndGrade(String studentCode, String grade);

    long countByCourse_Code(String courseCode);

    // Number of students in every course
    @Query("SELECT c.code AS courseCode, c.title AS title, COUNT(e) AS studentCount " +
           "FROM Enrollment e JOIN e.course c " +
           "GROUP BY c.code, c.title " +
           "ORDER BY COUNT(e) DESC, c.code")
    List<CourseEnrollmentCount> countStudentsPerCourse();
}