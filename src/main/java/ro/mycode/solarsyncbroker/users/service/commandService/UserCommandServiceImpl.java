package ro.mycode.solarsyncbroker.users.service.commandService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.mycode.solarsyncbroker.users.dtos.UserRequest;
import ro.mycode.solarsyncbroker.users.dtos.UserResponse;
import ro.mycode.solarsyncbroker.users.exceptions.UserAlreadyexistsException;
import ro.mycode.solarsyncbroker.users.mapper.UserMapper;
import ro.mycode.solarsyncbroker.users.model.User;
import ro.mycode.solarsyncbroker.users.repository.UserRepository;

@Service
public class UserCommandServiceImpl implements UserCommandService {
    UserRepository userRepository;
    public UserCommandServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserResponse addUser(UserRequest userRequest) {
        if(userRepository.existsUserByEmail((userRequest.email()))){
            throw new UserAlreadyexistsException();
        }
        User user = User.builder()
                .firstName(userRequest.firstName())
                .lastName(userRequest.lastName())
                .email(userRequest.email())
                .build();

        userRepository.save(user);
        return UserMapper.toResponse(userRepository.save(user));

    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, User user) {
        return null;
    }

    @Override
    @Transactional
    public UserResponse patchUser(Long id, User user) {
        return null;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {

    }
}
