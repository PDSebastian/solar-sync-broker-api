package ro.mycode.solarsyncbroker.users.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.mycode.solarsyncbroker.users.dtos.UserRequest;
import ro.mycode.solarsyncbroker.users.dtos.UserResponse;

import ro.mycode.solarsyncbroker.users.repository.UserRepository;
import ro.mycode.solarsyncbroker.users.service.commandService.UserCommandService;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {
    private UserCommandService userCommandService;



    @PostMapping("/add")
    public ResponseEntity<UserResponse> addUser( @RequestBody UserRequest userRequest){
          UserResponse userResponse=  userCommandService.addUser(userRequest);
            return ResponseEntity.status(HttpStatus.CREATED).build();

    }
}
