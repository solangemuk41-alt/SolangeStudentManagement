package org.example.studentmanagement.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class StudentManager {
    private final List<UserAccount> users = FileManager.loadUsers();
    private final List<Student> students = FileManager.loadStudents();
    private final List<Teacher> teachers = FileManager.loadTeachers();
    private final List<Course> courses = FileManager.loadCourses();
    private final List<Enrollment> enrollments = FileManager.loadEnrollments();

    public StudentManager() {
        ensureDefaultSupervisor();
    }

    public void signUpStudent(String fullName, String username, String password, int age) {
        if (findUser(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists.");
        }
        users.add(new UserAccount(username, password, UserRole.STUDENT, fullName));
        students.add(new Student(username, fullName, age));
        FileManager.saveUsers(users);
        FileManager.saveStudents(students);
    }

    public void addTeacherBySupervisor(String supervisorUsername, String fullName, String username, String password, int age, String subject) {
        UserAccount supervisor = requireUser(supervisorUsername);
        if (supervisor.getRole() != UserRole.SUPERVISOR) {
            throw new IllegalArgumentException("Only supervisor can create teachers.");
        }
        if (findUser(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists.");
        }
        users.add(new UserAccount(username, password, UserRole.TEACHER, fullName));
        Teacher teacher = new Teacher(username, fullName, age, subject);
        teachers.add(teacher);
        FileManager.saveUsers(users);
        FileManager.saveTeachers(teachers);
    }

    public void createCourseAndAssignTeacher(String supervisorUsername, String courseName, String teacherUsername) {
        UserAccount supervisor = requireUser(supervisorUsername);
        if (supervisor.getRole() != UserRole.SUPERVISOR) {
            throw new IllegalArgumentException("Only supervisor can assign courses.");
        }
        if (findCourse(courseName).isPresent()) {
            throw new IllegalArgumentException("Course already exists.");
        }
        boolean teacherExists = teachers.stream().anyMatch(t -> t.getUsername().equalsIgnoreCase(teacherUsername));
        if (!teacherExists) {
            throw new IllegalArgumentException("Teacher username not found.");
        }
        Course course = new Course(courseName, teacherUsername);
        courses.add(course);
        FileManager.saveCourses(courses);
    }

    public UserAccount login(String username, String password) {
        Optional<UserAccount> user = findUser(username);
        if (user.isEmpty() || !user.get().getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        return user.get();
    }

    public List<Course> getCoursesForStudentSignup() {
        return courses;
    }

    public List<Enrollment> getEnrollmentsForTeacher(String teacherUsername) {
        List<Enrollment> result = new LinkedList<>();
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getTeacherUsername().equalsIgnoreCase(teacherUsername)) {
                result.add(enrollment);
            }
        }
        return result;
    }

    public List<Enrollment> getEnrollmentsForStudent(String studentUsername) {
        List<Enrollment> result = new LinkedList<>();
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getStudentUsername().equalsIgnoreCase(studentUsername)) {
                result.add(enrollment);
            }
        }
        return result;
    }

    public void enrollStudentInCourse(String studentUsername, String courseName) {
        Course course = findCourse(courseName).orElseThrow(() -> new IllegalArgumentException("Course not found."));
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getStudentUsername().equalsIgnoreCase(studentUsername)
                    && enrollment.getCourseName().equalsIgnoreCase(courseName)) {
                throw new IllegalArgumentException("Already enrolled in this course.");
            }
        }
        enrollments.add(new Enrollment(studentUsername.toLowerCase(), course.getCourseName(), course.getTeacherUsername()));
        FileManager.saveEnrollments(enrollments);
    }

    public void assignGrade(String teacherUsername, String studentUsername, String courseName, double marks) {
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getTeacherUsername().equalsIgnoreCase(teacherUsername)
                    && enrollment.getStudentUsername().equalsIgnoreCase(studentUsername)
                    && enrollment.getCourseName().equalsIgnoreCase(courseName)) {
                enrollment.setMarks(marks);
                FileManager.saveEnrollments(enrollments);
                return;
            }
        }
        throw new IllegalArgumentException("Enrollment not found for this teacher.");
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public boolean removeTeacherBySupervisor(String supervisorUsername, String teacherUsername) {
        UserAccount supervisor = requireUser(supervisorUsername);
        if (supervisor.getRole() != UserRole.SUPERVISOR) {
            throw new IllegalArgumentException("Only supervisor can remove teachers.");
        }
        Teacher teacher = findTeacher(teacherUsername)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found."));

        teachers.remove(teacher);
        users.removeIf(user -> user.getUsername().equalsIgnoreCase(teacherUsername));
        courses.removeIf(course -> course.getTeacherUsername().equalsIgnoreCase(teacherUsername));
        enrollments.removeIf(enrollment -> enrollment.getTeacherUsername().equalsIgnoreCase(teacherUsername));

        FileManager.saveTeachers(teachers);
        FileManager.saveUsers(users);
        FileManager.saveCourses(courses);
        FileManager.saveEnrollments(enrollments);
        return true;
    }

    public Optional<Teacher> findTeacher(String username) {
        return teachers.stream().filter(t -> t.getUsername().equalsIgnoreCase(username)).findFirst();
    }

    public List<Teacher> searchTeachersByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return teachers;
        }
        String needle = keyword.trim().toLowerCase();
        List<Teacher> result = new ArrayList<>();
        for (Teacher teacher : teachers) {
            if (teacher.getUsername().contains(needle)
                    || teacher.getName().toLowerCase().contains(needle)
                    || teacher.getSubject().toLowerCase().contains(needle)) {
                result.add(teacher);
            }
        }
        return result;
    }

    public List<Teacher> getTeachersSortedByName() {
        return GenericListUtils.sortedCopy(teachers, Comparator.comparing(Teacher::getName, String.CASE_INSENSITIVE_ORDER));
    }

    public List<Course> getCourses() {
        return courses;
    }

    public Optional<UserAccount> findUser(String username) {
        return users.stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst();
    }

    private Optional<Course> findCourse(String name) {
        return courses.stream().filter(c -> c.getCourseName().equalsIgnoreCase(name)).findFirst();
    }

    private UserAccount requireUser(String username) {
        return findUser(username).orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    private void ensureDefaultSupervisor() {
        if (users.stream().noneMatch(u -> u.getRole() == UserRole.SUPERVISOR)) {
            users.add(new UserAccount("supervisor", "admin123", UserRole.SUPERVISOR, "Main Supervisor"));
            FileManager.saveUsers(users);
        }
    }
}
