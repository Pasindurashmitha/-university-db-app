package com.example.university_db_app.repository;

import com.example.university_db_app.entity.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LecturerRepository extends JpaRepository<Lecturer, Long> {

    Optional<Lecturer> findByEmail(String email);

    // property path: Lecturer.department.code
    List<Lecturer> findByDepartment_Code(String departmentCode);
}