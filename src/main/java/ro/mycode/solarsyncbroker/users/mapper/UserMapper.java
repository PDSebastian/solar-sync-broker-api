package ro.mycode.solarsyncbroker.users.mapper;

import org.springframework.stereotype.Component;
import ro.mycode.solarsyncbroker.users.dtos.UserResponse;
import ro.mycode.solarsyncbroker.users.model.User;

@Component
public class UserMapper {
    public static UserResponse toResponse(User user) {
        if(user==null){
            return null;
        }
        return new UserResponse(
                .id(user.getId())
                .
        )
    }
}
