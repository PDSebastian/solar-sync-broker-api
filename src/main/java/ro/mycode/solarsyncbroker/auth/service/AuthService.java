package ro.mycode.solarsyncbroker.auth.service;

import ro.mycode.solarsyncbroker.auth.dtos.AuthLoginrequest;
import ro.mycode.solarsyncbroker.auth.dtos.AuthReqisterRequest;
import ro.mycode.solarsyncbroker.users.dtos.UserResponse;

public interface AuthService {
    UserResponse register(AuthReqisterRequest authReqisterRequest);
    UserResponse login(AuthLoginrequest authLoginrequest);
}
