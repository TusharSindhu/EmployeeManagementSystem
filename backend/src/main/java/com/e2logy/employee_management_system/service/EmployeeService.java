package com.e2logy.employee_management_system.service;

import com.e2logy.employee_management_system.dto.EmployeeRequest;
import com.e2logy.employee_management_system.dto.EmployeeResponse;
import com.e2logy.employee_management_system.dto.ManagerOptionResponse;
import com.e2logy.employee_management_system.dto.PageResponse;
import com.e2logy.employee_management_system.entity.Department;
import com.e2logy.employee_management_system.entity.Employee;
import com.e2logy.employee_management_system.exception.DuplicateResourceException;
import com.e2logy.employee_management_system.exception.ResourceNotFoundException;
import com.e2logy.employee_management_system.repository.EmployeeRepository;
import com.e2logy.employee_management_system.specification.EmployeeSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        validateCreateDuplicates(request);

        Employee employee = new Employee();
        applyRequest(employee, request, resolveManager(request.managerId()));

        return toResponse(employeeRepository.save(employee));
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        return toResponse(findEmployeeById(id));
    }

    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponse> getEmployees(
            String search,
            Department department,
            Long managerId,
            LocalDate joiningDateFrom,
            LocalDate joiningDateTo,
            Pageable pageable
    ) {
        if (joiningDateFrom != null && joiningDateTo != null && joiningDateFrom.isAfter(joiningDateTo)) {
            throw new IllegalArgumentException("Joining date from must be on or before joining date to");
        }

        Page<EmployeeResponse> employeePage = employeeRepository.findAll(
                        EmployeeSpecification.withFilters(search, department, managerId, joiningDateFrom, joiningDateTo),
                        pageable
                )
                .map(this::toResponse);

        return new PageResponse<>(
                employeePage.getContent(),
                employeePage.getNumber(),
                employeePage.getSize(),
                employeePage.getTotalElements(),
                employeePage.getTotalPages(),
                employeePage.isFirst(),
                employeePage.isLast()
        );
    }

    @Transactional(readOnly = true)
    public List<ManagerOptionResponse> getManagerOptions() {
        return employeeRepository.findAllByOrderByFullNameAsc().stream()
                .map(employee -> new ManagerOptionResponse(
                        employee.getId(),
                        employee.getFullName(),
                        employee.getEmployeeCode()
                ))
                .toList();
    }

    public List<Department> getDepartmentOptions() {
        return List.of(Department.values());
    }

    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = findEmployeeById(id);
        validateUpdateDuplicates(id, request);

        Employee manager = resolveManager(request.managerId());
        validateManagerHierarchy(id, manager);
        applyRequest(employee, request, manager);

        return toResponse(employeeRepository.save(employee));
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = findEmployeeById(id);
        List<Employee> directReports = employeeRepository.findAllByManagerId(id);
        for (Employee directReport : directReports) {
            directReport.setManager(null);
        }

        employeeRepository.delete(employee);
    }

    private void validateCreateDuplicates(EmployeeRequest request) {
        if (employeeRepository.existsByEmployeeCode(request.employeeCode())) {
            throw new DuplicateResourceException("Employee code already exists");
        }
        if (employeeRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already exists");
        }
    }

    private void validateUpdateDuplicates(Long id, EmployeeRequest request) {
        if (employeeRepository.existsByEmployeeCodeAndIdNot(request.employeeCode(), id)) {
            throw new DuplicateResourceException("Employee code already exists");
        }
        if (employeeRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new DuplicateResourceException("Email already exists");
        }
    }

    private Employee resolveManager(Long managerId) {
        if (managerId == null) {
            return null;
        }
        return findEmployeeById(managerId);
    }

    private void validateManagerHierarchy(Long employeeId, Employee proposedManager) {
        if (proposedManager == null) {
            return;
        }

        Set<Long> visitedEmployeeIds = new HashSet<>();
        Employee currentManager = proposedManager;
        while (currentManager != null) {
            Long currentManagerId = currentManager.getId();
            if (employeeId.equals(currentManagerId)) {
                throw new IllegalArgumentException("An employee cannot report to themselves or one of their direct reports");
            }
            if (!visitedEmployeeIds.add(currentManagerId)) {
                throw new IllegalArgumentException("The proposed manager hierarchy contains a reporting cycle");
            }
            currentManager = currentManager.getManager();
        }
    }

    private Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
    }

    private void applyRequest(Employee employee, EmployeeRequest request, Employee manager) {
        employee.setEmployeeCode(request.employeeCode());
        employee.setFullName(request.fullName());
        employee.setDepartment(request.department());
        employee.setManager(manager);
        employee.setJoiningDate(request.joiningDate());
        employee.setEmail(request.email());
        employee.setPhoneNumber(request.phoneNumber());
    }

    private EmployeeResponse toResponse(Employee employee) {
        ManagerOptionResponse managerResponse = null;
        if (employee.getManager() != null) {
            Employee manager = employee.getManager();
            managerResponse = new ManagerOptionResponse(
                    manager.getId(),
                    manager.getFullName(),
                    manager.getEmployeeCode()
            );
        }

        return new EmployeeResponse(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getFullName(),
                employee.getDepartment(),
                managerResponse,
                employee.getJoiningDate(),
                employee.getEmail(),
                employee.getPhoneNumber()
        );
    }
}
