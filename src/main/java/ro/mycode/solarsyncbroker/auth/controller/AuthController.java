package ro.mycode.solarsyncbroker.auth.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mycode.solarsyncbroker.auth.dtos.AuthLoginrequest;
import ro.mycode.solarsyncbroker.auth.dtos.AuthReqisterRequest;
import ro.mycode.solarsyncbroker.auth.service.AuthService;
import ro.mycode.solarsyncbroker.users.dtos.UserResponse;
import ro.mycode.solarsyncbroker.users.service.UserDetailServiceImpl;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserDetailServiceImpl userQueryService;

    public AuthController(AuthService authService, UserDetailServiceImpl userQueryService) {
        this.authService = authService;
        this.userQueryService = userQueryService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody AuthReqisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody AuthLoginrequest request) {
        return ResponseEntity.ok(authService.login(request));
    }


}