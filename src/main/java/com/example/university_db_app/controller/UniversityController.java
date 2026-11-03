
package com.example.university_db_app.controller;

import com.example.university_db_app.dto.CourseCountDto;
import com.example.university_db_app.dto.CourseDto;
import com.example.university_db_app.dto.DepartmentDto;
import com.example.university_db_app.dto.EnrollmentDto;
import com.example.university_db_app.dto.LecturerDto;
import com.example.university_db_app.dto.StudentDto;

import com.example.university_db_app.entity.Course;
import com.example.university_db_app.entity.Department;

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

    // =========================
    // STUDENTS
    // =========================

    @GetMapping("/students")
    public List<StudentDto> students(
            @RequestParam(required = false) String name) {

        var list = (name == null || name.isBlank())
                ? studentRepository.findAllByOrderByFullNameAsc()
                : studentRepository.findByFullNameContainingIgnoreCase(name.trim());

        return list.stream()
                .map(StudentDto::from)
                .toList();
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

    // =========================
    // COURSES
    // =========================

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

    // =========================
    // DEPARTMENTS
    // =========================

    // View department lecturers
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

    // View all departments
    @GetMapping("/departments")
    public List<DepartmentDto> departments() {

        return departmentRepository.findAll()
                .stream()
                .map(DepartmentDto::from)
                .toList();
    }

    // View one department
    @GetMapping("/departments/{code}")
    public ResponseEntity<DepartmentDto> department(
            @PathVariable String code) {

        return departmentRepository.findByCode(code)
                .map(d -> ResponseEntity.ok(DepartmentDto.from(d)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Add department
    @PostMapping("/departments")
    public ResponseEntity<?> addDepartment(
            @RequestBody Department department) {

        if (department.getCode() == null ||
                department.getCode().isBlank() ||
                department.getName() == null ||
                department.getName().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("Department code and name are required.");
        }

        String code = department.getCode().trim();

        if (departmentRepository.findByCode(code).isPresent()) {
            return ResponseEntity.badRequest()
                    .body("Department code already exists.");
        }

        department.setId(null);
        department.setCode(code);
        department.setName(department.getName().trim());

        Department saved = departmentRepository.save(department);

        return ResponseEntity.status(201)
                .body(DepartmentDto.from(saved));
    }

    // Update department
    @PutMapping("/departments/{code}")
    public ResponseEntity<?> updateDepartment(
            @PathVariable String code,
            @RequestBody Department request) {

        var existing = departmentRepository.findByCode(code);

        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (request.getName() == null ||
                request.getName().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("Department name is required.");
        }

        Department department = existing.get();
        department.setName(request.getName().trim());

        Department updated = departmentRepository.save(department);

        return ResponseEntity.ok(DepartmentDto.from(updated));
    }

    // Delete department
    @DeleteMapping("/departments/{code}")
    @Transactional
    public ResponseEntity<?> deleteDepartment(
            @PathVariable String code) {

        var existing = departmentRepository.findWithLecturers(code);

        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (existing.get().getLecturers() != null &&
                !existing.get().getLecturers().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Cannot delete a department that has lecturers.");
        }

        departmentRepository.delete(existing.get());

        return ResponseEntity.noContent().build();
    }

    // =========================
    // ENROLLMENTS
    // =========================

    @GetMapping("/enrollments")
    @Transactional(readOnly = true)
    public List<EnrollmentDto> enrollments(
            @RequestParam(required = false) String grade) {

        if (grade == null || grade.isBlank()) {

            return enrollmentRepository.findAll()
                    .stream()
                    .map(EnrollmentDto::from)
                    .toList();
        }

        return enrollmentRepository.findByGrade(grade.trim())
                .stream()
                .map(EnrollmentDto::from)
                .toList();
    }

    // =========================
    // REPORTS
    // =========================

    @GetMapping("/reports/course-counts")
    public List<CourseCountDto> courseCounts() {

        return enrollmentRepository.countStudentsPerCourse()
                .stream()
                .map(CourseCountDto::from)
                .toList();
    }
}
