package com.example.university_db_app.repository;

import com.example.university_db_app.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByCode(String code);

    // JOIN FETCH loads the department and its lecturers with one SQL query
    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.lecturers WHERE d.code = :code")
    Optional<Department> findWithLecturers(@Param("code") String code);
}