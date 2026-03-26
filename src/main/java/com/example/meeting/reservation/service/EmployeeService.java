package com.example.meeting.reservation.service;

import com.example.meeting.reservation.common.DatabaseConstraintName;
import com.example.meeting.reservation.common.ExceptionUtils;
import com.example.meeting.reservation.dto.EmployeeCreateRequest;
import com.example.meeting.reservation.entity.Employee;
import com.example.meeting.reservation.exception.DuplicateEmployeeUsernameException;
import com.example.meeting.reservation.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;

import static com.example.meeting.reservation.common.StringNormalizer.normalize;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(EmployeeCreateRequest request) {
        var username = normalize(request.username());
        var password = passwordEncoder.encode(normalize(request.password()));
        var name = normalize(request.name());
        var employee = Employee.createStaff(username, password, name);

        try {
            var savedEmployee = employeeRepository.save(employee);
            log.info("회원가입 완료 id={}", savedEmployee.getId());
        } catch (DataIntegrityViolationException e) {
            var constraintName = ExceptionUtils.findConstraintName(e);
            if (DatabaseConstraintName.Employee.USERNAME.equals(constraintName)) {
                throw new DuplicateEmployeeUsernameException("이미 사용 중인 아이디입니다.");
            }
            throw e;
        }
    }

    public boolean isUsernameAvailable(String username) {
        var normalizedUsername = normalize(username);
        return !employeeRepository.existsByUsername(normalizedUsername);
    }
}
