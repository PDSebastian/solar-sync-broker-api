package ro.mycode.solarsyncbroker.unitTests.controllerTests;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.mycode.solarsyncbroker.auth.controller.AuthController;
import ro.mycode.solarsyncbroker.auth.service.AuthService;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTests {


    @Mock
    private AuthService authService;
    @InjectMocks
    private AuthController authController;






}
