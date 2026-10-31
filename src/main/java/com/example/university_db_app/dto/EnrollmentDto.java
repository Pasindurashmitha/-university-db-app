package com.example.university_db_app.dto;

import com.example.university_db_app.entity.Enrollment;

public record EnrollmentDto(
        String studentCode,
        String courseCode,
        String grade
) {
    public static EnrollmentDto from(Enrollment e) {
        return new EnrollmentDto(
                e.getStudent().getStudentCode(),
                e.getCourse().getCode(),
                e.getGrade()
        );
    }
}
