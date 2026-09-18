package com.example.tools;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;



public class PasswordUtil {

    private static final BCryptPasswordEncoder encoder =
            new BCryptPasswordEncoder(12);

    public static String hash(String password) {
        return encoder.encode(password);
    }

    public static boolean verify(String raw, String hash) {
        return encoder.matches(raw, hash);
    }
}