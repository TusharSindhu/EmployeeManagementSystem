package com.e2logy.employee_management_system.specification;

import com.e2logy.employee_management_system.entity.Department;
import com.e2logy.employee_management_system.entity.Employee;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class EmployeeSpecification {

    private EmployeeSpecification() {
    }

    public static Specification<Employee> withFilters(
            String search,
            Department department,
            Long managerId,
            LocalDate joiningDateFrom,
            LocalDate joiningDateTo
    ) {
        return (root, query, criteriaBuilder) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(search)) {
                String searchPattern = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("employeeCode")), searchPattern)
                ));
            }
            if (department != null) {
                predicates.add(criteriaBuilder.equal(root.get("department"), department));
            }
            if (managerId != null) {
                predicates.add(criteriaBuilder.equal(root.get("manager").get("id"), managerId));
            }
            if (joiningDateFrom != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("joiningDate"), joiningDateFrom));
            }
            if (joiningDateTo != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("joiningDate"), joiningDateTo));
            }

            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
