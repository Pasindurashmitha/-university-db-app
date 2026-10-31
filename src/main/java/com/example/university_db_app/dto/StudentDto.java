package com.example.university_db_app.dto;

import com.example.university_db_app.entity.Student;

public record StudentDto(
        String studentCode,
        String fullName,
        String email
) {
    public static StudentDto from(Student s) {
        return new StudentDto(
                s.getStudentCode(),
                s.getFullName(),
                s.getEmail()
        );
    }
}
