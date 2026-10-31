package com.example.university_db_app.repository;

import com.example.university_db_app.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCode(String code);

    List<Course> findByCreditsGreaterThanEqual(Integer credits);

    List<Course> findByTitleContainingIgnoreCase(String text);

    List<Course> findByLecturer_Name(String lecturerName);

    List<Course> findByLecturer_Department_Name(String departmentName);

    // Fetch courses together with their lecturers
    @Query("SELECT c FROM Course c JOIN FETCH c.lecturer")
    List<Course> findAllWithLecturer();

    @Query("SELECT c FROM Course c JOIN FETCH c.lecturer l WHERE l.department.name = :department")
    List<Course> findByDepartmentWithLecturer(@Param("department") String department);
}