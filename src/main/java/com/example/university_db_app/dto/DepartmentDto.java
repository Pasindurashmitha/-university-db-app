package com.example.university_db_app.dto;

import com.example.university_db_app.entity.Department;

public record DepartmentDto(
        String code,
        String name
) {
    public static DepartmentDto from(Department d) {
        return new DepartmentDto(
                d.getCode(),
                d.getName()
        );
    }
}
