package com.example.meeting.reservation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "employees")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Employee extends BaseEntity {
    @NotBlank
    @Size(max = 20)
    private String username;
    @NotBlank
    @Size(max = 255)
    private String password;
    @NotBlank
    @Size(max = 20)
    private String name;
    @NotNull
    @Enumerated(EnumType.STRING)
    private EmployeeRole role;

    private Employee(String username, String password, String name, EmployeeRole role) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public static Employee createStaff(String username, String password, String name) {
        return new Employee(username, password, name, EmployeeRole.STAFF);
    }
}