package com.example.meetingroom.entity;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("ADMIN"),
    MEMBER("MEMBER"),
    ;

    public final String name;

    Role(String name) {
        this.name = name;
    }
}