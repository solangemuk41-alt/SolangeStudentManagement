package org.example.studentmanagement.model;

public class UserAccount {
    private final String username;
    private final String password;
    private final UserRole role;
    private final String fullName;

    public UserAccount(String username, String password, UserRole role, String fullName) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Password must have at least 4 characters.");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name is required.");
        }
        this.username = username.trim().toLowerCase();
        this.password = password;
        this.role = role;
        this.fullName = fullName.trim();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

    public String getFullName() {
        return fullName;
    }
}
