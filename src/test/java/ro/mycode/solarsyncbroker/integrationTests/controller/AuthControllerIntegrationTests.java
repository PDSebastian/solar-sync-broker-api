package ro.mycode.solarsyncbroker.integrationTests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ro.mycode.solarsyncbroker.auth.dtos.AuthLoginrequest;
import ro.mycode.solarsyncbroker.users.model.User;
import ro.mycode.solarsyncbroker.users.model.UserType;
import ro.mycode.solarsyncbroker.users.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUpUser(){
        User user = User.builder()
                .firstName("Andrei")
                .lastName("Pop")
                .email("andrei1.popescu4@solarsync.ro")
                .password(passwordEncoder.encode("ParolaSecurizata1234567!"))
                .userType(UserType.USER)
                .build();

        userRepository.save(user);
    }
    @Test
    void loginSuccess() throws Exception{
        AuthLoginrequest authLoginrequest=new AuthLoginrequest("andrei1.popescu4@solarsync.ro","ParolaSecurizata1234567!");
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authLoginrequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("andrei1.popescu4@solarsync.ro"));
    }
    @Test
    void loginReturns401() throws Exception {
        AuthLoginrequest authLoginrequest=new AuthLoginrequest("PopAndrei@.com","1234");
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authLoginrequest)))
                .andExpect(status().isUnauthorized());


    }
    @Test
    void testlogingWithInvalidPassword() throws Exception{
        AuthLoginrequest authLoginrequest=new AuthLoginrequest("PopAndrei@.com","1234");
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authLoginrequest)))
                .andExpect(status().isUnauthorized());


    }



}
