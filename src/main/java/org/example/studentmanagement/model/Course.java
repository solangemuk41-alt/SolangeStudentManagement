package org.example.studentmanagement.model;

public class Course {
    private final String courseName;
    private final String teacherUsername;

    public Course(String courseName, String teacherUsername) {
        if (courseName == null || courseName.isBlank()) {
            throw new IllegalArgumentException("Course name cannot be empty.");
        }
        if (teacherUsername == null || teacherUsername.isBlank()) {
            throw new IllegalArgumentException("Teacher is required.");
        }
        this.courseName = courseName.trim();
        this.teacherUsername = teacherUsername.trim().toLowerCase();
    }

    public String getCourseName() {
        return courseName;
    }

    public String getTeacherUsername() {
        return teacherUsername;
    }
}
