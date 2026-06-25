package ro.mycode.solarsyncbroker.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ro.mycode.solarsyncbroker.auth.dtos.AuthLoginrequest;
import ro.mycode.solarsyncbroker.auth.dtos.AuthReqisterRequest;
import ro.mycode.solarsyncbroker.users.dtos.UserResponse;
import ro.mycode.solarsyncbroker.users.exceptions.UserAlreadyexistsException;
import ro.mycode.solarsyncbroker.users.model.User;
import ro.mycode.solarsyncbroker.users.repository.UserRepository;
import ro.mycode.solarsyncbroker.users.service.commandService.UserCommandService;

@Component
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private UserCommandService  userCommandService;


    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,UserCommandService userCommandService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userCommandService = userCommandService;
    }

    @Override
    public UserResponse register(AuthReqisterRequest authReqisterRequest) {
        if(userRepository.findByEmail(authReqisterRequest.email()).isPresent()){
            throw new UserAlreadyexistsException();
        }
        User user = User.builder()
                .firstName(authReqisterRequest.firstName())
                .lastName(authReqisterRequest.lastName())
                .email(authReqisterRequest.email())
                .password(passwordEncoder.encode(authReqisterRequest.password()))
                .build();

        User savedUser = userRepository.save(user);
      return new UserResponse(
              savedUser.getId(),
              savedUser.getFirstName(),
              savedUser.getLastName(),
              savedUser.getPermissions()
      );



    }

    @Override
    public UserResponse login(AuthLoginrequest authLoginrequest) {
        return null;
    }
}
