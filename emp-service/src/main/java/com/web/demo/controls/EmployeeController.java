package com.web.demo.controls;

import com.web.audit.entities.ServiceAudit;
import com.web.audit.services.AuditService;
import com.web.demo.entities.Employee;
import com.web.demo.services.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AuditService auditService;

    @GetMapping
    public List<Employee> getAllEmployees() {
        LOGGER.info("============getAllEmployees called starting=========");
        List<ServiceAudit> serviceAuditList = auditService.findAll();
        LOGGER.info("============serviceAuditList size========={}",serviceAuditList.size());
        return employeeService.getAllEmployees();
    }

    @GetMapping("/emp/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable("id") Long id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        return employee.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/emp")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        Employee createdEmployee = employeeService.createEmployee(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    @PutMapping("/emp/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable("id") Long id, @RequestBody Employee employeeDetails) {
        Employee updatedEmployee = employeeService.updateEmployee(id, employeeDetails);
        return updatedEmployee != null ? ResponseEntity.ok(updatedEmployee)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/emp/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable("id") Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}

