package com.example.meeting.reservation.common.util;

public final class StringNormalizer {
    private StringNormalizer() {}

    public static String normalize(String str) {
        return str.strip();
    }
}