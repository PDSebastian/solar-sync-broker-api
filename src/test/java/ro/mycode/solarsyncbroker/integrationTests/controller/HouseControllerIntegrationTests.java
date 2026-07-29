package ro.mycode.solarsyncbroker.integrationTests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ro.mycode.solarsyncbroker.house.dtos.HouseRequest;
import ro.mycode.solarsyncbroker.house.model.House;
import ro.mycode.solarsyncbroker.house.repository.HouseRepository;
import ro.mycode.solarsyncbroker.users.model.User;
import ro.mycode.solarsyncbroker.users.model.UserType;
import ro.mycode.solarsyncbroker.users.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc()
@ActiveProfiles("test")
@Transactional
public class HouseControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private UserRepository userRepository;

    private Long userId;
    private Long houseId;

    @BeforeEach
    void setDataBase(){
        User user = User.builder()
                .firstName("Pop")
                .lastName("Andrei")
                .email("andrei1.popescu@solarsync.ro")
                .password("123")
                .userType(UserType.USER)
                .build();

        userId = userRepository.save(user).getId();

        House house = House.builder()
                .name("Casa1")
                .pvPeakPowerKw(5.5)
                .maxImportPowerKw(10.0)
                .maxExportPowerKw(4.0)
                .enabled(true)
                .owner(user)
                .build();

        houseId = houseRepository.save(house).getId();
    }

    @Test
    @WithMockUser(authorities = {"house:view"})
    void getAllHouses() throws Exception {
        mockMvc.perform(get("/api/v1/houses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Casa1"))
                .andExpect(jsonPath("$[0].ownerId").value(userId));
    }

    @Test
    @WithMockUser(authorities = {"house:manage"})
    void createHouse() throws Exception {
        HouseRequest houseRequest = new HouseRequest("Casa2", 6.0, 12.0, 5.5, userId);

        mockMvc.perform(post("/api/v1/houses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(houseRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Casa2"))
                .andExpect(jsonPath("$.pvPeakPowerKw").value(6.0));
    }

    @Test
    @WithMockUser(authorities = {"house:manage"})
    void updateHouse() throws Exception {
        HouseRequest houseRequest = new HouseRequest("Casa2", 6.0, 12.0, 5.5, userId);
        mockMvc.perform(put("/api/v1/houses/" + houseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(houseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Casa2"))
                .andExpect(jsonPath("$.pvPeakPowerKw").value(6.0));
    }

    @Test
    @WithMockUser(authorities = {"house:manage"})
    void patchHouse() throws Exception {
        HouseRequest houseRequest= new HouseRequest("Casa2", 6.0, 12.0, 5.5, userId);
        mockMvc.perform(patch("/api/v1/houses/" + houseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(houseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Casa2"))
                .andExpect(jsonPath("$.pvPeakPowerKw").value(6.0));
    }

    @Test
    @WithMockUser(authorities = {"house:manage"})
    void deleteHouse() throws Exception {
        mockMvc.perform(delete("/api/v1/houses/" + houseId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetHouseByIdReturnsUnotharized() throws Exception {
        mockMvc.perform(get("/api/v1/houses/" + houseId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = {"FARA_DREPTURI"})
    void testGetHouseByIdWithoughtAuthorisation() throws Exception {
        mockMvc.perform(get("/api/v1/houses/" + houseId))
                .andExpect(status().isForbidden());
    }


}