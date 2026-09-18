package com.example.code.version1.authandextraction ;


public class JwtExtractorService {

private static final String PREFIX  = "Bearer ";

public static String jwtExtactormethod(String header) {

    if (header == null || header.isBlank()) {
        return null;
    }

    if (!header.startsWith(PREFIX)) {
        return null;
    }

    String token = header.substring(PREFIX.length()).trim();

    return token.isEmpty() ? null : token;
}

}