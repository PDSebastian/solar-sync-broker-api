package ro.mycode.solarsyncbroker.users.service.commandService;

import ro.mycode.solarsyncbroker.users.dtos.UserRequest;
import ro.mycode.solarsyncbroker.users.dtos.UserResponse;
import ro.mycode.solarsyncbroker.users.model.User;

public interface UserCommandService {
    UserResponse addUser(UserRequest userRequest);
    UserResponse updateUser(Long id,User user);
    UserResponse patchUser(Long id,User user);
    void deleteUser(Long id);
}
