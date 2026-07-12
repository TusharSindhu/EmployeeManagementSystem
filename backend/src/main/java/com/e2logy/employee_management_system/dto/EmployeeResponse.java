package com.e2logy.employee_management_system.dto;

import com.e2logy.employee_management_system.entity.Department;

import java.time.LocalDate;

public record EmployeeResponse(
        Long id,
        String employeeCode,
        String fullName,
        Department department,
        ManagerOptionResponse manager,
        LocalDate joiningDate,
        String email,
        String phoneNumber
) {
}
