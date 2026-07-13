package com.e2logy.employee_management_system.controller;

import com.e2logy.employee_management_system.dto.EmployeeResponse;
import com.e2logy.employee_management_system.dto.PageResponse;
import com.e2logy.employee_management_system.entity.Department;
import com.e2logy.employee_management_system.exception.GlobalExceptionHandler;
import com.e2logy.employee_management_system.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
@Import(GlobalExceptionHandler.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Test
    void createsEmployeeAndReturnsCreated() throws Exception {
        when(employeeService.createEmployee(any())).thenReturn(employeeResponse());

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.employeeCode").value("E001"));
    }

    @Test
    void returnsBadRequestForInvalidCreateRequest() throws Exception {
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.fieldErrors.employeeCode").value("Employee code is required"));
    }

    @Test
    void returnsPagedEmployeeList() throws Exception {
        PageResponse<EmployeeResponse> pageResponse = new PageResponse<>(
                List.of(employeeResponse()), 0, 10, 1, 1, true, true
        );
        when(employeeService.getEmployees(any(), any(), any(), any(), any(), any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/employees").param("search", "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].fullName").value("Alice"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void rejectsNegativePage() throws Exception {
        mockMvc.perform(get("/api/employees").param("page", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Page must be zero or greater"));
    }

    @Test
    void rejectsInvalidSize() throws Exception {
        mockMvc.perform(get("/api/employees").param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Size must be between 1 and 100"));
    }

    @Test
    void rejectsUnsupportedSortField() throws Exception {
        mockMvc.perform(get("/api/employees").param("sortBy", "email"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unsupported sort field: email"));
    }

    @Test
    void rejectsInvalidSortDirection() throws Exception {
        mockMvc.perform(get("/api/employees").param("direction", "sideways"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Sort direction must be asc or desc"));
    }

    @Test
    void deletesEmployeeAndReturnsNoContent() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isNoContent());

        verify(employeeService).deleteEmployee(1L);
    }

    private EmployeeResponse employeeResponse() {
        return new EmployeeResponse(
                1L,
                "E001",
                "Alice",
                Department.IT,
                null,
                LocalDate.of(2024, 1, 15),
                "alice@example.com",
                "+91 9876543210"
        );
    }

    private String validRequestJson() {
        return """
                {
                  "employeeCode": "E001",
                  "fullName": "Alice",
                  "department": "IT",
                  "joiningDate": "2024-01-15",
                  "email": "alice@example.com",
                  "phoneNumber": "+91 9876543210"
                }
                """;
    }
}
