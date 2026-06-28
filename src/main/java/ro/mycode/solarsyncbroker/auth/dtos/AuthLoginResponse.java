package ro.mycode.solarsyncbroker.auth.dtos;

import ro.mycode.solarsyncbroker.system.security.UserPermissions;
import java.util.Set;

public record AuthLoginResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        Set<UserPermissions> directPermissions,
        String token
) {}