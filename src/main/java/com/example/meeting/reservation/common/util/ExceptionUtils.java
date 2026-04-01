package com.example.meeting.reservation.common.util;

import org.hibernate.exception.ConstraintViolationException;

public final class ExceptionUtils {
    private ExceptionUtils() {}

    public static String findConstraintName(Throwable throwable) {
        for(var cur = throwable; cur != null; cur = cur.getCause()) {
            if(cur instanceof ConstraintViolationException cve) {
                return cve.getConstraintName();
            }
        }
        return null;
    }
}
