package com.example.mailclient.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for validating email addresses.
 */
public class EmailValidator {
    private static final String EMAIL_PATTERN =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    private static final Pattern p = Pattern.compile(EMAIL_PATTERN);

    /**
     * @brief Validates an email address using a regex pattern.
     * @param email The email address to validate.
     * @return true if the email is valid, false otherwise.
     */
    public static boolean checkEmail(String email) {
        if (email == null) return false;
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
