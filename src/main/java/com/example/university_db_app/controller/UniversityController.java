
package com.example.university_db_app.controller;

import com.example.university_db_app.dto.CourseCountDto;
import com.example.university_db_app.dto.CourseDto;
import com.example.university_db_app.dto.EnrollmentDto;
import com.example.university_db_app.dto.LecturerDto;
import com.example.university_db_app.dto.StudentDto;
import com.example.university_db_app.entity.Course;
import com.example.university_db_app.repository.CourseRepository;
import com.example.university_db_app.repository.DepartmentRepository;
import com.example.university_db_app.repository.EnrollmentRepository;
import com.example.university_db_app.repository.StudentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UniversityController {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final EnrollmentRepository enrollmentRepository;

    // Students
    @GetMapping("/students")
    public List<StudentDto> students(
            @RequestParam(required = false) String name) {

        var list = (name == null || name.isBlank())
                ? studentRepository.findAllByOrderByFullNameAsc()
                : studentRepository.findByFullNameContainingIgnoreCase(name.trim());

        return list.stream().map(StudentDto::from).toList();
    }

    @GetMapping("/students/{code}")
    public ResponseEntity<StudentDto> student(
            @PathVariable String code) {

        return studentRepository.findByStudentCode(code)
                .map(s -> ResponseEntity.ok(StudentDto.from(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/students/{code}/enrollments")
    @Transactional(readOnly = true)
    public List<EnrollmentDto> studentEnrollments(
            @PathVariable String code) {

        return enrollmentRepository.findByStudent_StudentCode(code)
                .stream()
                .map(EnrollmentDto::from)
                .toList();
    }

    // Courses
    @GetMapping("/courses")
    @Transactional(readOnly = true)
    public List<CourseDto> courses(
            @RequestParam(required = false) Integer minCredits,
            @RequestParam(required = false) String department) {

        List<Course> list;

        if (minCredits != null) {
            list = courseRepository.findByCreditsGreaterThanEqual(minCredits);
        } else if (department != null && !department.isBlank()) {
            list = courseRepository.findByDepartmentWithLecturer(
                    department.trim());
        } else {
            list = courseRepository.findAllWithLecturer();
        }

        return list.stream()
                .map(CourseDto::from)
                .toList();
    }

    @GetMapping("/courses/{code}/students")
    @Transactional(readOnly = true)
    public List<StudentDto> courseStudents(
            @PathVariable String code) {

        return enrollmentRepository.findByCourse_Code(code)
                .stream()
                .map(e -> StudentDto.from(e.getStudent()))
                .toList();
    }

    // Departments
    @GetMapping("/departments/{code}/lecturers")
    @Transactional(readOnly = true)
    public ResponseEntity<List<LecturerDto>> departmentLecturers(
            @PathVariable String code) {

        return departmentRepository.findWithLecturers(code)
                .map(d -> ResponseEntity.ok(
                        d.getLecturers().stream()
                                .map(LecturerDto::from)
                                .toList()))
                .orElse(ResponseEntity.notFound().build());
    }

    // Enrollments and reports
    @GetMapping("/enrollments")
    @Transactional(readOnly = true)
    public List<EnrollmentDto> enrollments(
            @RequestParam String grade) {

        return enrollmentRepository.findByGrade(grade)
                .stream()
                .map(EnrollmentDto::from)
                .toList();
    }

    @GetMapping("/reports/course-counts")
    public List<CourseCountDto> courseCounts() {

        return enrollmentRepository.countStudentsPerCourse()
                .stream()
                .map(CourseCountDto::from)
                .toList();
    }
}
