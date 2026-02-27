package com.example.mailclient.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {
    private static final String EMAIL_PATTERN =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    private static final Pattern p = Pattern.compile(EMAIL_PATTERN);

    public static boolean checkEmail(String email) {
        if (email == null) return false;
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
