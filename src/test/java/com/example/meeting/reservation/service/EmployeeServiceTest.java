package com.example.meeting.reservation.service;

import com.example.meeting.reservation.common.util.DatabaseConstraintName;
import com.example.meeting.reservation.dto.EmployeeCreateRequest;
import com.example.meeting.reservation.entity.Employee;
import com.example.meeting.reservation.exception.DuplicateEmployeeUsernameException;
import com.example.meeting.reservation.repository.EmployeeRepository;
import org.assertj.core.api.Assertions;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private EmployeeService employeeService;

    @Test
    @DisplayName("회원 가입 - 성공")
    void 회원가입_성공() {
        var request = new EmployeeCreateRequest("test", "test1234", "테스트");
        var employee = Employee.createStaff(request.username(), request.password(), request.name());
        when(passwordEncoder.encode(anyString())).thenReturn("test1234");
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        employeeService.signup(request);

        verify(passwordEncoder, times(1)).encode(anyString());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("회원 가입 - 실패(아이디 중복)")
    void 회원가입_실패_아이디_중복() {
        var request = new EmployeeCreateRequest("test", "test1234", "테스트");
        var exception = createDataIntegrityViolationException(DatabaseConstraintName.Employee.USERNAME);
        when(employeeRepository.save(any(Employee.class))).thenThrow(exception);

        Assertions
                .assertThatThrownBy(() -> employeeService.signup(request))
                .isExactlyInstanceOf(DuplicateEmployeeUsernameException.class)
                .hasMessage("이미 사용 중인 아이디입니다.");
    }

    private DataIntegrityViolationException createDataIntegrityViolationException(String constraintName) {
        var cve = new ConstraintViolationException("msg", null, constraintName);
        return new DataIntegrityViolationException("msg", cve);
    }
}