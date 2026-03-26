package com.example.meeting.reservation.repository;

import com.example.meeting.reservation.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, EmployeeRepositoryCustom {
    boolean existsByUsername(String username);
    Optional<Employee> findByUsername(String username);
}
