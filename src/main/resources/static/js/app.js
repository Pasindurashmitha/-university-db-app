
// =====================================
// UNIVERSITY DATABASE APPLICATION
// =====================================

const API_BASE_URL = "/api";

let allStudents = [];
let allCourses = [];
let allLecturers = [];
let allEnrollments = [];
// =====================================
// API REQUEST FUNCTION
// =====================================

async function fetchData(endpoint) {
    const response = await fetch(`${API_BASE_URL}${endpoint}`);

    if (!response.ok) {
        throw new Error(`${endpoint} API failed: ${response.status}`);
    }

    return response.json();
}

// =====================================
// DASHBOARD
// =====================================

function updateStatistics(students, courses, departments, enrollments) {
    document.getElementById("totalStudents").textContent = students.length;
    document.getElementById("totalCourses").textContent = courses.length;
    document.getElementById("totalDepartments").textContent = departments.length;
    document.getElementById("totalEnrollments").textContent = enrollments.length;
}

function displayStudents(students) {
    const tableBody = document.getElementById("studentTable");
    tableBody.replaceChildren();

    students.slice(0, 5).forEach(student => {
        const row = document.createElement("tr");

        const code = document.createElement("td");
        code.textContent = student.studentCode;

        const name = document.createElement("td");
        name.textContent = student.fullName;

        const email = document.createElement("td");
        email.textContent = student.email || "—";

        row.append(code, name, email);
        tableBody.appendChild(row);
    });

    if (students.length === 0) {
        tableBody.innerHTML =
            '<tr><td colspan="3">No students found.</td></tr>';
    }
}

async function loadDashboard() {
    try {
        const [students, courses, departments, enrollments] =
            await Promise.all([
                fetchData("/students"),
                fetchData("/courses"),
                fetchData("/departments"),
                fetchData("/enrollments?grade=A")
            ]);

        updateStatistics(students, courses, departments, enrollments);
        displayStudents(students);

    } catch (error) {
        console.error("Dashboard loading failed:", error);

        document.getElementById("studentTable").innerHTML =
            '<tr><td colspan="3">Unable to load dashboard data.</td></tr>';

        document.getElementById("totalStudents").textContent = "Error";
        document.getElementById("totalCourses").textContent = "Error";
        document.getElementById("totalDepartments").textContent = "Error";
        document.getElementById("totalEnrollments").textContent = "Error";
    }
}

// =====================================
// STUDENTS PAGE
// =====================================

async function loadStudents() {
    const tableBody = document.getElementById("allStudentsTable");

    tableBody.innerHTML =
        '<tr><td colspan="3">Loading students...</td></tr>';

    try {
        allStudents = await fetchData("/students");

        document.getElementById("studentCount").textContent =
            `${allStudents.length} Students`;

        displayAllStudents(allStudents);

    } catch (error) {
        console.error("Students loading failed:", error);

        tableBody.innerHTML =
            '<tr><td colspan="3">Unable to load students.</td></tr>';
    }
}

function displayAllStudents(students) {
    const tableBody = document.getElementById("allStudentsTable");
    tableBody.replaceChildren();

    if (students.length === 0) {
        tableBody.innerHTML =
            '<tr><td colspan="3">No students found.</td></tr>';
        return;
    }

    students.forEach(student => {
        const row = document.createElement("tr");

        const code = document.createElement("td");
        code.textContent = student.studentCode;

        const name = document.createElement("td");
        name.textContent = student.fullName;

        const email = document.createElement("td");
        email.textContent = student.email || "—";

        row.append(code, name, email);
        row.classList.add("clickable-row");

        row.addEventListener("click", () => {
            showStudentDetails(student.studentCode);
        });

        tableBody.appendChild(row);
    });
}

// =====================================
// STUDENT SEARCH
// =====================================

function setupStudentSearch() {
    const searchInput = document.getElementById("studentSearch");

    if (!searchInput) return;

    searchInput.addEventListener("input", event => {
        const searchValue = event.target.value.toLowerCase().trim();

        const filteredStudents = allStudents.filter(student =>
            (student.studentCode || "").toLowerCase().includes(searchValue) ||
            (student.fullName || "").toLowerCase().includes(searchValue) ||
            (student.email || "").toLowerCase().includes(searchValue)
        );

        displayAllStudents(filteredStudents);
    });
}

// =====================================
// COURSES PAGE
// =====================================

async function loadCourses() {
    const tableBody = document.getElementById("allCoursesTable");

    tableBody.innerHTML =
        '<tr><td colspan="4">Loading courses...</td></tr>';

    try {
        allCourses = await fetchData("/courses");

        document.getElementById("courseCount").textContent =
            `${allCourses.length} Courses`;

        displayAllCourses(allCourses);

    } catch (error) {
        console.error("Courses loading failed:", error);

        tableBody.innerHTML =
            '<tr><td colspan="4">Unable to load courses.</td></tr>';
    }
}

function displayAllCourses(courses) {
    const tableBody = document.getElementById("allCoursesTable");
    tableBody.replaceChildren();

    if (courses.length === 0) {
        tableBody.innerHTML =
            '<tr><td colspan="4">No courses found.</td></tr>';
        return;
    }

    courses.forEach(course => {
        const row = document.createElement("tr");

        const codeCell = document.createElement("td");
        codeCell.textContent = course.code;

        const titleCell = document.createElement("td");
        titleCell.textContent = course.title;

        const creditsCell = document.createElement("td");
        creditsCell.textContent = course.credits;

        const lecturerCell = document.createElement("td");
        lecturerCell.textContent = course.lecturer || "—";

        row.append(codeCell, titleCell, creditsCell, lecturerCell);
        tableBody.appendChild(row);
    });
}

// =====================================
// COURSE SEARCH
// =====================================

function setupCourseSearch() {
    const searchInput = document.getElementById("courseSearch");

    if (!searchInput) return;

    searchInput.addEventListener("input", event => {
        const searchValue = event.target.value.toLowerCase().trim();

        const filteredCourses = allCourses.filter(course =>
            (course.code || "").toLowerCase().includes(searchValue) ||
            (course.title || "").toLowerCase().includes(searchValue) ||
            (course.lecturer || "").toLowerCase().includes(searchValue)
        );

        displayAllCourses(filteredCourses);
    });
}

// =====================================
// DEPARTMENTS PAGE
// =====================================

async function loadDepartments() {
    const tableBody = document.getElementById("departmentTable");

    tableBody.innerHTML =
        '<tr><td colspan="3">Loading departments...</td></tr>';

    try {
        const departments = await fetchData("/departments");

        const departmentDetails = await Promise.all(
            departments.map(async department => {
                const lecturers = await fetchData(
                    `/departments/${department.code}/lecturers`
                );

                return {
                    code: department.code,
                    name: department.name,
                    lecturerCount: lecturers.length
                };
            })
        );

        tableBody.replaceChildren();

        departmentDetails.forEach(department => {
            const row = document.createElement("tr");

            const codeCell = document.createElement("td");
            codeCell.textContent = department.code;

            const nameCell = document.createElement("td");
            nameCell.textContent = department.name;

            const lecturerCell = document.createElement("td");
            lecturerCell.textContent = department.lecturerCount;

            row.append(codeCell, nameCell, lecturerCell);
            tableBody.appendChild(row);
        });

        document.getElementById("departmentCount").textContent =
            `${departments.length} Departments`;

        if (departments.length === 0) {
            tableBody.innerHTML =
                '<tr><td colspan="3">No departments found.</td></tr>';
        }

    } catch (error) {
        console.error("Department loading failed:", error);

        tableBody.innerHTML =
            '<tr><td colspan="3">Unable to load departments.</td></tr>';
    }
}

/
// =====================================
// ENROLLMENTS PAGE
// =====================================

async function loadEnrollments() {
    const tableBody = document.getElementById("enrollmentTable");
    const countElement = document.getElementById("enrollmentCount");

    tableBody.innerHTML =
        '<tr><td colspan="5">Loading enrollments...</td></tr>';

    try {
        const [enrollments, students, courses] = await Promise.all([
            fetchData("/enrollments"),
            fetchData("/students"),
            fetchData("/courses")
        ]);

        // Prepare enrollment data with student and course details
        allEnrollments = enrollments.map(enrollment => {
            const student = students.find(
                s => s.studentCode === enrollment.studentCode
            );

            const course = courses.find(
                c => c.code === enrollment.courseCode
            );

            return {
                studentCode: enrollment.studentCode || "—",
                studentName: student ? student.fullName : "Unknown",
                courseCode: enrollment.courseCode || "—",
                courseTitle: course ? course.title : "Unknown",
                grade: enrollment.grade || "Pending"
            };
        });

        displayEnrollments(allEnrollments);

    } catch (error) {
        console.error("Enrollment loading failed:", error);

        tableBody.innerHTML =
            '<tr><td colspan="5">Unable to load enrollments.</td></tr>';

        countElement.textContent = "0 Enrollments";
    }
}

// Display enrollments in the table
function displayEnrollments(enrollments) {
    const tableBody = document.getElementById("enrollmentTable");
    const countElement = document.getElementById("enrollmentCount");

    tableBody.replaceChildren();

    countElement.textContent =
        `${enrollments.length} Enrollments`;

    if (enrollments.length === 0) {
        tableBody.innerHTML =
            '<tr><td colspan="5">No enrollments found.</td></tr>';
        return;
    }

    enrollments.forEach(enrollment => {
        const row = document.createElement("tr");

        const studentCodeCell = document.createElement("td");
        studentCodeCell.textContent = enrollment.studentCode;

        const studentNameCell = document.createElement("td");
        studentNameCell.textContent = enrollment.studentName;

        const courseCodeCell = document.createElement("td");
        courseCodeCell.textContent = enrollment.courseCode;

        const courseTitleCell = document.createElement("td");
        courseTitleCell.textContent = enrollment.courseTitle;

        const gradeCell = document.createElement("td");
        gradeCell.textContent = enrollment.grade;

        row.append(
            studentCodeCell,
            studentNameCell,
            courseCodeCell,
            courseTitleCell,
            gradeCell
        );

        tableBody.appendChild(row);
    });
}

// =====================================
// ENROLLMENT LIVE SEARCH
// =====================================

function setupEnrollmentSearch() {
    const searchInput = document.getElementById("enrollmentSearch");

    if (!searchInput) return;

    searchInput.addEventListener("input", event => {
        const searchValue = event.target.value.toLowerCase().trim();

        const filteredEnrollments = allEnrollments.filter(enrollment =>
            enrollment.studentCode.toLowerCase().includes(searchValue) ||
            enrollment.studentName.toLowerCase().includes(searchValue) ||
            enrollment.courseCode.toLowerCase().includes(searchValue) ||
            enrollment.courseTitle.toLowerCase().includes(searchValue) ||
            enrollment.grade.toLowerCase().includes(searchValue)
        );

        displayEnrollments(filteredEnrollments);
    });
}


// =====================================
// OLD LECTURER DISPLAY FUNCTION
// =====================================

function displayLecturers(lecturers) {
    const tableBody = document.getElementById("lecturerTable");
    if (!tableBody) return;

    tableBody.replaceChildren();

    if (lecturers.length === 0) {
        tableBody.innerHTML =
            '<tr><td colspan="3">No lecturers found.</td></tr>';
        return;
    }

    lecturers.forEach(lecturer => {
        const row = document.createElement("tr");

        const nameCell = document.createElement("td");
        nameCell.textContent = lecturer.name;

        const emailCell = document.createElement("td");
        emailCell.textContent = lecturer.email || "—";

        const departmentCell = document.createElement("td");
        departmentCell.textContent = lecturer.department || "—";

        row.append(nameCell, emailCell, departmentCell);
        tableBody.appendChild(row);
    });
}

// =====================================
// STUDENT DETAILS MODAL
// =====================================

async function showStudentDetails(studentCode) {
    const modal = document.getElementById("studentModal");
    modal.hidden = false;

    document.getElementById("detailName").textContent = "Loading...";
    document.getElementById("detailCode").textContent = "";
    document.getElementById("detailStudentCode").textContent = "Loading...";
    document.getElementById("detailFullName").textContent = "Loading...";
    document.getElementById("detailEmail").textContent = "Loading...";

    const enrollmentTable = document.getElementById("detailEnrollments");

    enrollmentTable.innerHTML =
        '<tr><td colspan="2">Loading enrollments...</td></tr>';

    try {
        const [student, enrollments] = await Promise.all([
            fetchData(`/students/${encodeURIComponent(studentCode)}`),
            fetchData(`/students/${encodeURIComponent(studentCode)}/enrollments`)
        ]);

        document.getElementById("detailAvatar").textContent =
            (student.fullName || "S").charAt(0).toUpperCase();

        document.getElementById("detailName").textContent =
            student.fullName || "—";

        document.getElementById("detailCode").textContent =
            student.studentCode || "—";

        document.getElementById("detailStudentCode").textContent =
            student.studentCode || "—";

        document.getElementById("detailFullName").textContent =
            student.fullName || "—";

        document.getElementById("detailEmail").textContent =
            student.email || "—";

        enrollmentTable.replaceChildren();

        if (enrollments.length === 0) {
            enrollmentTable.innerHTML =
                '<tr><td colspan="2">No courses enrolled.</td></tr>';
            return;
        }

        enrollments.forEach(enrollment => {
            const row = document.createElement("tr");

            const courseCell = document.createElement("td");
            courseCell.textContent = enrollment.courseCode;

            const gradeCell = document.createElement("td");
            gradeCell.textContent = enrollment.grade || "Pending";

            row.append(courseCell, gradeCell);
            enrollmentTable.appendChild(row);
        });

    } catch (error) {
        console.error("Student details error:", error);

        document.getElementById("detailName").textContent =
            "Unable to load student details";

        enrollmentTable.innerHTML =
            '<tr><td colspan="2">Unable to load course enrollments.</td></tr>';
    }
}

// =====================================
// CLOSE STUDENT DETAILS MODAL
// =====================================

function setupStudentModal() {
    const closeButton = document.getElementById("closeStudentModal");
    const modal = document.getElementById("studentModal");

    if (closeButton && modal) {
        closeButton.addEventListener("click", () => {
            modal.hidden = true;
        });

        modal.addEventListener("click", event => {
            if (event.target === modal) {
                modal.hidden = true;
            }
        });
    }
}

// =====================================
// PAGE NAVIGATION
// =====================================

const pageDescriptions = {
    dashboard: "Welcome to your university management system.",
    students: "Manage and view registered university students.",
    courses: "View available university courses.",
    departments: "Explore academic departments.",
    enrollments: "View student course registrations."
};

function showPage(pageName) {

    document.querySelectorAll(".page-section").forEach(section => {
        section.hidden = true;
    });

    document.querySelectorAll(".nav-link").forEach(link => {
        link.classList.remove("active");
    });

    const selectedPage = document.getElementById(`${pageName}Page`);
    if (!selectedPage) return;

    selectedPage.hidden = false;

    document.getElementById("pageTitle").textContent =
        pageName.charAt(0).toUpperCase() + pageName.slice(1);

    document.getElementById("pageDescription").textContent =
        pageDescriptions[pageName] || "";

    const selectedLink = document.querySelector(
        `.nav-link[data-page="${pageName}"]`
    );

    if (selectedLink) {
        selectedLink.classList.add("active");
    }

    if (pageName === "students") loadStudents();
    if (pageName === "courses") loadCourses();
    if (pageName === "departments") loadDepartments();
    if (pageName === "enrollments") loadEnrollments();
}

// =====================================
// INITIALIZE APPLICATION
// =====================================

document.addEventListener("DOMContentLoaded", () => {

    setupStudentSearch();
    setupCourseSearch();
    setupEnrollmentSearch();
    setupStudentModal();

    document.querySelectorAll(".nav-link").forEach(link => {
        link.addEventListener("click", event => {
            event.preventDefault();
            showPage(link.dataset.page);
        });
    });

    loadDashboard();
    showPage("dashboard");
});
