package org.example.studentmanagement.model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

class FileManager {
    private static final Path USERS_FILE = Path.of("users.txt");
    private static final Path STUDENTS_FILE = Path.of("students.txt");
    private static final Path TEACHERS_FILE = Path.of("teachers.txt");
    private static final Path COURSES_FILE = Path.of("courses.txt");
    private static final Path ENROLLMENTS_FILE = Path.of("enrollments.txt");

    static void saveUsers(List<UserAccount> users) {
        List<String> lines = new ArrayList<>();
        for (UserAccount user : users) {
            lines.add(user.getUsername() + "," + user.getPassword() + "," + user.getRole().name() + "," + user.getFullName());
        }
        writeLines(USERS_FILE, lines);
    }

    static List<UserAccount> loadUsers() {
        List<UserAccount> users = new ArrayList<>();
        for (String line : readLines(USERS_FILE)) {
            String[] parts = line.split(",", -1);
            if (parts.length < 4) {
                continue;
            }
            try {
                users.add(new UserAccount(parts[0], parts[1], UserRole.valueOf(parts[2]), parts[3]));
            } catch (Exception ignored) {
                // Skip malformed rows.
            }
        }
        return users;
    }

    static void saveStudents(List<Student> students) {
        List<String> lines = new ArrayList<>();
        for (Student s : students) {
            lines.add(s.getUsername() + "," + s.getName() + "," + s.getAge());
        }
        writeLines(STUDENTS_FILE, lines);
    }

    static List<Student> loadStudents() {
        List<Student> students = new ArrayList<>();
        for (String line : readLines(STUDENTS_FILE)) {
            String[] parts = line.split(",", -1);
            if (parts.length < 3) {
                continue;
            }
            try {
                students.add(new Student(parts[0], parts[1], Integer.parseInt(parts[2])));
            } catch (Exception ignored) {
                // Skip malformed rows to keep loading resilient.
            }
        }
        return students;
    }

    static void saveTeachers(List<Teacher> teachers) {
        List<String> lines = new ArrayList<>();
        for (Teacher teacher : teachers) {
            lines.add(teacher.getUsername() + "," + teacher.getName() + "," + teacher.getAge() + "," + teacher.getSubject());
        }
        writeLines(TEACHERS_FILE, lines);
    }

    static List<Teacher> loadTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        for (String line : readLines(TEACHERS_FILE)) {
            String[] parts = line.split(",", -1);
            if (parts.length < 4) {
                continue;
            }
            try {
                teachers.add(new Teacher(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3]));
            } catch (Exception ignored) {
                // Skip malformed rows.
            }
        }
        return teachers;
    }

    static void saveCourses(List<Course> courses) {
        List<String> lines = new ArrayList<>();
        for (Course course : courses) {
            lines.add(course.getCourseName() + "," + course.getTeacherUsername());
        }
        writeLines(COURSES_FILE, lines);
    }

    static List<Course> loadCourses() {
        List<Course> courses = new ArrayList<>();
        for (String line : readLines(COURSES_FILE)) {
            String[] parts = line.split(",", -1);
            if (parts.length < 2) {
                continue;
            }
            if (!parts[0].isBlank()) {
                courses.add(new Course(parts[0], parts[1]));
            }
        }
        return courses;
    }

    static void saveEnrollments(List<Enrollment> enrollments) {
        List<String> lines = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            String marks = enrollment.getMarks() == null ? "" : enrollment.getMarks().toString();
            lines.add(enrollment.getStudentUsername() + "," + enrollment.getCourseName() + "," + enrollment.getTeacherUsername() + "," + marks);
        }
        writeLines(ENROLLMENTS_FILE, lines);
    }

    static List<Enrollment> loadEnrollments() {
        List<Enrollment> enrollments = new ArrayList<>();
        for (String line : readLines(ENROLLMENTS_FILE)) {
            String[] parts = line.split(",", -1);
            if (parts.length < 3) {
                continue;
            }
            try {
                Enrollment enrollment = new Enrollment(parts[0], parts[1], parts[2]);
                if (parts.length > 3 && !parts[3].isBlank()) {
                    enrollment.setMarks(Double.parseDouble(parts[3]));
                }
                enrollments.add(enrollment);
            } catch (Exception ignored) {
                // Skip malformed rows.
            }
        }
        return enrollments;
    }

    private static void writeLines(Path file, List<String> lines) {
        try {
            Files.write(file, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ignored) {
            // Persisting data is best-effort for desktop usage.
        }
    }

    private static List<String> readLines(Path file) {
        try {
            if (!Files.exists(file)) {
                return List.of();
            }
            return Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            return List.of();
        }
    }
}
