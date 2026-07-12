# Employee Management System

## Project Overview

This project is a web-based Employee Management System built as a Java Spring Boot practical assignment.

The application supports employee CRUD operations, registration, search, filtering, sorting, pagination, and tabular employee management.

The implementation should remain simple, maintainable, and easy to explain during a technical interview.

## Technology Stack

### Backend

- Java 17
- Spring Boot
- Maven
- Spring Web
- Spring Data JPA
- Jakarta Bean Validation
- MySQL
- Lombok

### Frontend

- React
- Vite
- JavaScript
- Native Fetch API
- Plain CSS

## Backend Architecture

Use the following package structure:

- controller
- service
- repository
- entity
- dto
- specification
- exception
- config

Controllers must not directly access repositories.

Business logic belongs in the service layer.

Repositories are responsible for database access.

Do not expose JPA entities directly from REST controllers.

Use request and response DTOs for employee APIs.

Prefer Java 17 records for DTOs when appropriate.

Use Spring Data JPA Specifications for dynamic employee filtering.

Pagination and sorting must be performed server-side.

Use Pageable and Page for pagination.

Use LocalDate for employee joining dates.

Use Jakarta Bean Validation for request validation.

Use a global exception handler for consistent API error responses.

## Employee Filtering Requirements

The employee list must support optional and composable filters for:

- Employee name or employee code search
- Department
- Manager
- Joining date from
- Joining date to

Do not create repository methods for every possible filter combination.

Use JPA Specifications for dynamic filtering.

## Frontend Architecture

Do not place the complete application inside App.jsx.

Use reusable components.

Expected components include:

- EmployeeTable
- EmployeeFilters
- EmployeeForm
- Pagination
- ConfirmDialog

Keep API calls in a dedicated API module.

Use native fetch unless another HTTP client is explicitly requested.

Use React state and hooks.

Do not add Redux.

Reuse EmployeeForm for both create and edit operations.

Search should be debounced before calling the backend.

Sorting, filtering, and pagination must call the backend rather than processing the complete employee dataset in React.

## Scope Restrictions

Do not add Spring Security or JWT unless explicitly requested.

Do not add Docker unless explicitly requested.

Do not add Kafka or RabbitMQ.

Do not introduce microservices.

Do not add Redux.

Do not add a frontend UI framework unless explicitly requested.

Avoid unnecessary abstractions and enterprise patterns.

The code must remain appropriate for a small interview practical assignment.

## Codex Working Instructions

Before making a major architectural change, explain the proposed approach.

Do not change the technology stack without asking.

Make focused changes related to the current task.

Do not refactor unrelated code.

After modifying files, summarize:

1. Files created
2. Files modified
3. Important implementation decisions

When reviewing code, identify:

- Possible bugs
- Validation issues
- Database query concerns
- Error handling problems
- Edge cases

Prefer code that the developer can clearly explain during an interview.

Do not generate the entire application in one step unless explicitly requested.