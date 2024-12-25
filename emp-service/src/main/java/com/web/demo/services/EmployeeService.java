package com.web.demo.services;

import com.web.demo.entities.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    List<Employee> getAllEmployees();

    Optional<Employee> getEmployeeById(Long id);

    Employee createEmployee(Employee employee);

    Employee updateEmployee(Long id, Employee employeeDetails);

    void deleteEmployee(Long id);
}
