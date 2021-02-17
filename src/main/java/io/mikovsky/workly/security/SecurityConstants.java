package io.mikovsky.workly.security;

public interface SecurityConstants {

    String AUTH_PATH = "/api/auth/**";
    String SECRET = "SecretKeyToGenerateJWTs";
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
    long EXPIRATION_TIME = 300_000; // 5 minutes
}
