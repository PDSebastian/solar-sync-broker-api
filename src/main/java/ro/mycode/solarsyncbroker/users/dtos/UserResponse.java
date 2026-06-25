package ro.mycode.solarsyncbroker.users.dtos;

import ro.mycode.solarsyncbroker.system.security.UserPermissions;

import java.util.Set;

public record UserResponse(
        Long id,
        String email,
        String password,
        String firstName,
        String lastName,
        int age,
        Set<UserPermissions>permissions


) {
}
