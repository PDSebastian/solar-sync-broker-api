package ro.mycode.solarsyncbroker.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.mycode.solarsyncbroker.auth.dtos.AuthLoginrequest;
import ro.mycode.solarsyncbroker.auth.dtos.AuthReqisterRequest;
import ro.mycode.solarsyncbroker.system.security.JWTTokenProvider;
import ro.mycode.solarsyncbroker.system.security.UserPermissions;
import ro.mycode.solarsyncbroker.users.dtos.UserResponse;
import ro.mycode.solarsyncbroker.users.exceptions.UserAlreadyexistsException;
import ro.mycode.solarsyncbroker.users.exceptions.UserNotFoundException;
import ro.mycode.solarsyncbroker.users.model.User;
import ro.mycode.solarsyncbroker.users.model.UserType;
import ro.mycode.solarsyncbroker.users.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository,
                           AuthenticationManager authenticationManager,
                           JWTTokenProvider jwtTokenProvider,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponse register(AuthReqisterRequest authReqisterRequest) {
        if (userRepository.findByEmail(authReqisterRequest.email()).isPresent()) {
            throw new UserAlreadyexistsException();
        }

        User user = User.builder()
                .firstName(authReqisterRequest.firstName())
                .lastName(authReqisterRequest.lastName())
                .email(authReqisterRequest.email())
                .password(passwordEncoder.encode(authReqisterRequest.password()))
                .age(authReqisterRequest.age())
                .userType(authReqisterRequest.userType())
                .build();

        user.setPermissions(permissionsForType(authReqisterRequest.userType()));
        User savedUser = userRepository.save(user);


        return new UserResponse(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.getUserType(),
                savedUser.getPermissions(),
                LocalDateTime.now(),
                null
        );

    }

    @Override
    @Transactional()
    public UserResponse login(AuthLoginrequest authLoginrequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authLoginrequest.email(), authLoginrequest.password())
        );

        User user = userRepository.findByEmail(authLoginrequest.email())
                .orElseThrow(() -> new UserNotFoundException());

        String token = jwtTokenProvider.generateToken(user);

        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUserType(),
                user.getPermissions(),
               LocalDateTime.now(),
                token
        );
    }

    private Set<UserPermissions> permissionsForType(UserType userType) {
        Set<UserPermissions> permissions = new HashSet<>();
        if (userType == UserType.ADMIN) {
            permissions.addAll(Set.of(
                    UserPermissions.HOUSE_VIEW, UserPermissions.HOUSE_MANAGE,
                    UserPermissions.BATTERY_VIEW, UserPermissions.BATTERY_MANAGE
            ));
        } else {
            permissions.addAll(Set.of(
                    UserPermissions.HOUSE_VIEW, UserPermissions.BATTERY_VIEW
            ));
        }
        return permissions;
    }
}