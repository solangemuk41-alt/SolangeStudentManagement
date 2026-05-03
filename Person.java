package org.example.studentmanagement.model;

public abstract class Person {
    protected String name;
    protected int age;

    protected Person(String name, int age) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required.");
        }
        if (age <= 0) {
            throw new InvalidAgeException("Age must be greater than zero.");
        }
        this.name = name.trim();
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
