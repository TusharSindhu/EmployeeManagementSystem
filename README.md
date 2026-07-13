# Employee Management System

A full-stack employee management application built with Java 17, Spring Boot, React, and MySQL. It is intentionally small and maintainable for a technical interview practical assignment.

## Features

- Employee registration and create, read, update, and delete operations
- Search by employee name or employee code
- Department and manager filtering
- Joining-date range filtering
- Server-side sorting and pagination
- Optional manager hierarchy
- Self-reference and reporting-cycle prevention
- Request validation and consistent API error responses
- Responsive employee-management interface with reusable form, filters, table, pagination, and delete confirmation

## Tech Stack

### Backend

- Java 17
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Jakarta Bean Validation
- MySQL
- Maven
- JUnit
- Mockito
- MockMvc

### Frontend

- React
- Vite
- JavaScript
- Native Fetch API
- Plain CSS

## Architecture

```text
React UI
  → REST API
  → Controller
  → Service
  → Repository / JPA Specifications
  → MySQL
```

- **Controller**: binds HTTP requests, validates request DTOs, and returns HTTP responses.
- **Service**: owns business rules, transactions, entity-to-DTO mapping, duplicate checks, and hierarchy validation.
- **Repository**: provides JPA persistence operations and server-side page queries.
- **Specifications**: compose optional search and filter predicates into one database query.

## Project Structure

```text
backend/
  src/main/java/com/e2logy/employee_management_system/
    controller/      Employee REST endpoints
    dto/             Request, response, and page records
    entity/          Employee and Department JPA model
    exception/       Consistent API error handling
    repository/      Spring Data repository
    service/         Employee business logic
    specification/   Dynamic employee filters
  src/test/          Service and controller tests

frontend/
  src/
    api/             Native Fetch API functions
    components/      Filters, table, form, pagination, dialog
    hooks/           Reusable debounce hook
    App.jsx          Page-level UI orchestration
```

## Database Setup

Create the MySQL database manually:

```sql
CREATE DATABASE employee_management_system
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

Hibernate manages the development schema with `spring.jpa.hibernate.ddl-auto=update`. Flyway is not used.

## Backend Setup

Requirements: Java 17 and a running MySQL server.

From the `backend` directory, set database credentials and start the application:

```bash
DB_USERNAME=root DB_PASSWORD='your_mysql_password' ./mvnw spring-boot:run
```

The backend starts on `http://localhost:8080`.

## Frontend Setup

From the `frontend` directory:

```bash
npm install
npm run dev
```

Vite proxies relative `/api` requests to `http://localhost:8080`, so the backend must be running there during development.

## API Endpoints

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `POST` | `/api/employees` | Create an employee |
| `GET` | `/api/employees` | List employees with filters, sorting, and pagination |
| `GET` | `/api/employees/{id}` | Get one employee |
| `PUT` | `/api/employees/{id}` | Update an employee |
| `DELETE` | `/api/employees/{id}` | Delete an employee |
| `GET` | `/api/employees/manager-options` | Manager dropdown options |
| `GET` | `/api/employees/departments` | Department enum options |

List query parameters:

- `search`, `department`, `managerId`, `joiningDateFrom`, `joiningDateTo`
- `page` (default `0`), `size` (default `10`)
- `sortBy` (`fullName`, `employeeCode`, `department`, or `joiningDate`)
- `direction` (`asc` or `desc`)

Example:

```text
GET /api/employees?search=alice&department=IT&managerId=2&joiningDateFrom=2024-01-01&joiningDateTo=2024-12-31&page=0&size=10&sortBy=fullName&direction=asc
```

## Testing

Run backend tests from the `backend` directory:

```bash
./mvnw test
```

The suite contains 10 `EmployeeService` Mockito unit tests and 8 `EmployeeController` MockMvc tests: 18 tests total. It does not claim MySQL integration coverage. The complete React/Spring Boot/MySQL CRUD and filtering flow has also been manually verified.

## Key Design Decisions

- **Self-referencing manager**: `Employee.manager` is nullable because top-level employees have no manager.
- **String department enum**: `EnumType.STRING` stores readable, stable department names in the database.
- **DTO API boundary**: controllers never expose JPA entities or recursive manager graphs.
- **Composable filters**: JPA Specifications avoid a repository method for every filter combination.
- **Server-side lists**: sorting and pagination use `Pageable`, keeping filtering and ordering in MySQL.
- **Safe hierarchy updates**: service logic rejects self-management and direct or indirect reporting cycles.
- **Transactional deletion**: direct reports have their manager reference cleared before their manager is deleted.
- **Development proxy**: Vite forwards `/api` requests to Spring Boot without frontend CORS setup.
