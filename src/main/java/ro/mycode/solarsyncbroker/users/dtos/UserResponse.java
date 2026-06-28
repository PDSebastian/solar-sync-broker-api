package ro.mycode.solarsyncbroker.users.dtos;

import ro.mycode.solarsyncbroker.system.security.UserPermissions;
import ro.mycode.solarsyncbroker.users.model.UserType;

import java.time.LocalDateTime;
import java.util.Set;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        UserType userType,
        Set<UserPermissions> permissions,
        LocalDateTime now,
        String token
)

{}
