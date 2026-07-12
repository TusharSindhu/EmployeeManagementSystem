package com.e2logy.employee_management_system.dto;

import com.e2logy.employee_management_system.entity.Department;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EmployeeRequest(
        @NotBlank(message = "Employee code is required")
        @Size(max = 30, message = "Employee code must not exceed 30 characters")
        @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "Employee code may contain only letters, numbers, and hyphens")
        String employeeCode,

        @NotBlank(message = "Full name is required")
        @Size(max = 120, message = "Full name must not exceed 120 characters")
        String fullName,

        @NotNull(message = "Department is required")
        Department department,

        @Positive(message = "Manager ID must be positive")
        Long managerId,

        @NotNull(message = "Joining date is required")
        @PastOrPresent(message = "Joining date cannot be in the future")
        LocalDate joiningDate,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 150, message = "Email must not exceed 150 characters")
        String email,

        @NotBlank(message = "Phone number is required")
        @Size(max = 30, message = "Phone number must not exceed 30 characters")
        @Pattern(regexp = "^[0-9+()\\-\\s]+$", message = "Phone number contains invalid characters")
        String phoneNumber
) {
}
