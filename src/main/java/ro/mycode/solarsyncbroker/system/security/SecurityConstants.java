package ro.mycode.solarsyncbroker.system.security;

import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SecurityConstants {
    public static final String TOKEN_PREFIX = "Bearer";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final String AUTHORITIES = "authorities";
    public static final String ISSUER = "controllerPractice-api";
    public static final String AUDIENCE = "controllerPractice-audience";
    public static final String[] PUBLIC_URLS = {
            "/api/v2/auth/login",
            "/api/v2/auth/register",
            " http://localhost:8080/login#/"
    };


}
