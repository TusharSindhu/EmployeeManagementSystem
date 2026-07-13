package com.e2logy.employee_management_system.service;

import com.e2logy.employee_management_system.dto.EmployeeRequest;
import com.e2logy.employee_management_system.dto.EmployeeResponse;
import com.e2logy.employee_management_system.entity.Department;
import com.e2logy.employee_management_system.entity.Employee;
import com.e2logy.employee_management_system.exception.DuplicateResourceException;
import com.e2logy.employee_management_system.exception.ResourceNotFoundException;
import com.e2logy.employee_management_system.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void createsEmployeeWithoutManager() {
        EmployeeRequest request = request(null);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
            Employee employee = invocation.getArgument(0);
            employee.setId(1L);
            return employee;
        });

        EmployeeResponse response = employeeService.createEmployee(request);

        assertEquals(1L, response.id());
        assertEquals("E001", response.employeeCode());
        assertNull(response.manager());
        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(employeeCaptor.capture());
        assertNull(employeeCaptor.getValue().getManager());
    }

    @Test
    void createsEmployeeWithManager() {
        Employee manager = employee(2L, "E002", "Manager");
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(manager));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
            Employee employee = invocation.getArgument(0);
            employee.setId(1L);
            return employee;
        });

        EmployeeResponse response = employeeService.createEmployee(request(2L));

        assertEquals(2L, response.manager().id());
        assertEquals("Manager", response.manager().fullName());
        verify(employeeRepository).findById(2L);
    }

    @Test
    void rejectsDuplicateEmployeeCodeOnCreate() {
        when(employeeRepository.existsByEmployeeCode("E001")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> employeeService.createEmployee(request(null))
        );

        assertEquals("Employee code already exists", exception.getMessage());
        verify(employeeRepository, never()).existsByEmail(any());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void rejectsDuplicateEmailOnCreate() {
        when(employeeRepository.existsByEmail("alice@example.com")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> employeeService.createEmployee(request(null))
        );

        assertEquals("Email already exists", exception.getMessage());
        verify(employeeRepository).existsByEmployeeCode("E001");
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void allowsUpdateWithUnchangedEmployeeCodeAndEmail() {
        Employee employee = employee(1L, "E001", "Alice");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);

        EmployeeResponse response = employeeService.updateEmployee(1L, request(null));

        assertEquals("E001", response.employeeCode());
        verify(employeeRepository).existsByEmployeeCodeAndIdNot("E001", 1L);
        verify(employeeRepository).existsByEmailAndIdNot("alice@example.com", 1L);
        verify(employeeRepository).save(employee);
    }

    @Test
    void rejectsMissingManager() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.createEmployee(request(99L))
        );

        assertEquals("Employee not found with ID: 99", exception.getMessage());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void rejectsSelfManagerAssignment() {
        Employee employee = employee(1L, "E001", "Alice");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.updateEmployee(1L, request(1L))
        );

        assertTrue(exception.getMessage().contains("cannot report to themselves"));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void rejectsDirectReportingCycle() {
        Employee employee = employee(1L, "E001", "Alice");
        Employee directReport = employee(2L, "E002", "Bob");
        directReport.setManager(employee);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(directReport));

        assertThrows(IllegalArgumentException.class, () -> employeeService.updateEmployee(1L, request(2L)));

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void rejectsIndirectReportingCycle() {
        Employee employee = employee(1L, "E001", "Alice");
        Employee directReport = employee(2L, "E002", "Bob");
        Employee indirectReport = employee(3L, "E003", "Carol");
        directReport.setManager(employee);
        indirectReport.setManager(directReport);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(3L)).thenReturn(Optional.of(indirectReport));

        assertThrows(IllegalArgumentException.class, () -> employeeService.updateEmployee(1L, request(3L)));

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void clearsDirectReportsBeforeDeletingManager() {
        Employee manager = employee(1L, "E001", "Manager");
        Employee reportOne = employee(2L, "E002", "Alice");
        Employee reportTwo = employee(3L, "E003", "Bob");
        reportOne.setManager(manager);
        reportTwo.setManager(manager);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(manager));
        when(employeeRepository.findAllByManagerId(1L)).thenReturn(List.of(reportOne, reportTwo));

        employeeService.deleteEmployee(1L);

        assertNull(reportOne.getManager());
        assertNull(reportTwo.getManager());
        var inOrder = inOrder(employeeRepository);
        inOrder.verify(employeeRepository).findById(1L);
        inOrder.verify(employeeRepository).findAllByManagerId(1L);
        inOrder.verify(employeeRepository).delete(manager);
    }

    private EmployeeRequest request(Long managerId) {
        return new EmployeeRequest(
                "E001",
                "Alice",
                Department.IT,
                managerId,
                LocalDate.of(2024, 1, 15),
                "alice@example.com",
                "+91 9876543210"
        );
    }

    private Employee employee(Long id, String employeeCode, String fullName) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setEmployeeCode(employeeCode);
        employee.setFullName(fullName);
        employee.setDepartment(Department.IT);
        employee.setJoiningDate(LocalDate.of(2024, 1, 15));
        employee.setEmail(employeeCode.toLowerCase() + "@example.com");
        employee.setPhoneNumber("9876543210");
        return employee;
    }
}
