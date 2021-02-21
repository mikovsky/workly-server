package io.mikovsky.workly.security;

public interface SecurityConstants {

    String SECRET = "SecretKeyToGenerateJWTs";
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
    long EXPIRATION_TIME = 900_000;

}
