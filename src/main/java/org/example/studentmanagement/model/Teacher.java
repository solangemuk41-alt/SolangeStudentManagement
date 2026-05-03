package org.example.studentmanagement.model;

public class Teacher extends Person {
    private final String username;
    private final String subject;

    public Teacher(String username, String name, int age, String subject) {
        super(name, age);
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Subject is required.");
        }
        this.username = username.trim().toLowerCase();
        this.subject = subject.trim();
    }

    public String getUsername() {
        return username;
    }

    public String getSubject() {
        return subject;
    }
}
