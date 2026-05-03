package org.example.studentmanagement.model;

public class Enrollment {
    private final String studentUsername;
    private final String courseName;
    private final String teacherUsername;
    private Double marks;

    public Enrollment(String studentUsername, String courseName, String teacherUsername) {
        this.studentUsername = studentUsername;
        this.courseName = courseName;
        this.teacherUsername = teacherUsername;
    }

    public String getStudentUsername() {
        return studentUsername;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getTeacherUsername() {
        return teacherUsername;
    }

    public Double getMarks() {
        return marks;
    }

    public void setMarks(Double marks) {
        this.marks = marks;
    }

    public String getMarksText() {
        return marks == null ? "Not graded" : String.format("%.2f", marks);
    }
}
