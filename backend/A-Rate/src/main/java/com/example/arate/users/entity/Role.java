package com.example.arate.users.entity;

public enum Role {
    ADMIN("관리자"),
    PROFESSOR("교수"),
    STUDENT("학생");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAuthority() {
        return "ROLE_" + name();
    }
} 