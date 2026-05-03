package org.example.studentmanagement.model;

public class Student extends Person {
    private final String username;

    public Student(String username, String name, int age) {
        super(name, age);
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }
        this.username = username.trim().toLowerCase();
    }

    public String getUsername() {
        return username;
    }
}
