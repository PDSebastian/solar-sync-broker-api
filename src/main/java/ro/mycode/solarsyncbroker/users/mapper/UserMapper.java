package ro.mycode.solarsyncbroker.users.mapper;

import ro.mycode.solarsyncbroker.users.dtos.UserResponse;
import ro.mycode.solarsyncbroker.users.model.User;

import java.time.LocalDateTime;

public class UserMapper {

    public static UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponse(
            user.getId(),
                user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getUserType(),
                        user.getPermissions(),
                        LocalDateTime.now(),
                        null
                        );
}
}