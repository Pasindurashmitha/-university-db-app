package com.example.university_db_app.repository;

import com.example.university_db_app.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCode(String code);

    // WHERE credits >= ?
    List<Course> findByCreditsGreaterThanEqual(Integer credits);

    List<Course> findByTitleContainingIgnoreCase(String text);

    // Course -> lecturer -> name
    List<Course> findByLecturer_Name(String lecturerName);

    // Course -> lecturer -> department -> name
    List<Course> findByLecturer_Department_Name(String departmentName);
}