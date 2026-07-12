package com.e2logy.employee_management_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(
        name = "employees",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_employees_employee_code", columnNames = "employee_code"),
                @UniqueConstraint(name = "uk_employees_email", columnNames = "email")
        },
        indexes = {
                @Index(name = "idx_employees_department", columnList = "department"),
                @Index(name = "idx_employees_manager_id", columnList = "manager_id"),
                @Index(name = "idx_employees_joining_date", columnList = "joining_date")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_code", nullable = false, length = 30)
    private String employeeCode;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "department", nullable = false, length = 30)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "manager_id", nullable = true)
    private Employee manager;

    @Column(name = "joining_date", nullable = false)
    private LocalDate joiningDate;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "phone_number", nullable = false, length = 30)
    private String phoneNumber;
}
