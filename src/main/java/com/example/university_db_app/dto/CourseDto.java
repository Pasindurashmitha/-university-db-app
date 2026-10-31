package com.example.university_db_app.dto;

import com.example.university_db_app.entity.Course;

public record CourseDto(
        String code,
        String title,
        Integer credits,
        String lecturer
) {
    public static CourseDto from(Course c) {
        return new CourseDto(
                c.getCode(),
                c.getTitle(),
                c.getCredits(),
                c.getLecturer().getName()
        );
    }
}
