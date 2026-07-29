package ro.mycode.solarsyncbroker.users.dtos;

import org.hibernate.usertype.UserType;

public record UserRequest(
        String email,
        String password,
        String firstName,
        String lastName,
        int age


) {
}
