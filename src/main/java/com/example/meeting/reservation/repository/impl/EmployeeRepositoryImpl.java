package com.example.meeting.reservation.repository.impl;

import com.example.meeting.reservation.repository.EmployeeRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static com.example.meeting.reservation.entity.QEmployee.employee;

@Repository
@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void updateLastLoginAt(String username, LocalDateTime lastLoginAt) {
        jpaQueryFactory.update(employee)
                .set(employee.lastLoginAt, lastLoginAt)
                .where(employee.username.eq(username))
                .execute();
    }
}
