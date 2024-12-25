package com.web.rest.emp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "EMPLOYEE")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMPNO")
    private Long empId;
    @Column(name = "ENAME")
    private String name;
    @Column(name = "SAL")
    private int salary;
    @Column(name = "MGR")
    private String manager;
    @Column(name = "JOB")
    private String job;
    @Column(name = "HIREDATE", nullable = false)
    private Date hiredate;
    @Column(name = "DEPTNO")
    private int deptNo;
    @Column(name = "COMM")
    private String comm;

}

