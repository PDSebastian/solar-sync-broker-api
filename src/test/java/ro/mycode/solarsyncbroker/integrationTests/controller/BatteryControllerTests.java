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
import ro.mycode.solarsyncbroker.battery.dtos.BatteryAction;
import ro.mycode.solarsyncbroker.battery.dtos.BatteryCommandRequest;
import ro.mycode.solarsyncbroker.battery.model.Battery;
import ro.mycode.solarsyncbroker.battery.repository.BatteryRepository;
import ro.mycode.solarsyncbroker.house.model.House;
import ro.mycode.solarsyncbroker.house.repository.HouseRepository;
import ro.mycode.solarsyncbroker.users.model.User;
import ro.mycode.solarsyncbroker.users.model.UserType;
import ro.mycode.solarsyncbroker.users.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BatteryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private BatteryRepository batteryRepository;

    private House savedHouse;

    @BeforeEach
    void setUp() {
        batteryRepository.deleteAll();
        houseRepository.deleteAll();
        userRepository.deleteAll();

        User admin = User.builder()
                .firstName("Admin")
                .lastName("Test")
                .email("admin@test.ro")
                .password("parola")
                .age(30)
                .userType(UserType.ADMIN)
                .build();
        User savedUser = userRepository.save(admin);

        House house = House.builder()
                .name("Casa Mea")
                .pvPeakPowerKw(5.0)
                .maxImportPowerKw(10.0)
                .maxExportPowerKw(10.0)
                .enabled(true)
                .owner(savedUser)
                .build();
        savedHouse = houseRepository.save(house);

        Battery battery = Battery.builder()
                .socPercent(50.0)
                .maxChargePowerKw(3.0)
                .maxDischargePowerKw(3.0)
                .efficientyPercent(95.0)
                .house(savedHouse)
                .build();
        batteryRepository.save(battery);
    }

    @Test
    @WithMockUser(username = "admin@test.ro", authorities = {"BATTERY_COMMAND"})
    void returncommand201delaproprietar() throws Exception {
        BatteryCommandRequest request = new BatteryCommandRequest(BatteryAction.CHARGE, 50, savedHouse.getId());

        mockMvc.perform(post("/api/v1/houses/" + savedHouse.getId() + "/battery/commands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "owner@test.ro", authorities = {"BATTERY_COMMAND"})
    void prorietardacommandainvalida400() throws Exception {
        User owner = userRepository.save(User.builder()
                .firstName("Owner").lastName("Test").email("owner@test.ro")
                .password("parola").age(30).userType(UserType.USER).build());

        House house = houseRepository.save(House.builder()
                .name("Casa Mea").pvPeakPowerKw(5.0)
                .maxImportPowerKw(10.0).maxExportPowerKw(10.0)
                .enabled(true).owner(owner).build());

        BatteryCommandRequest request = new BatteryCommandRequest(BatteryAction.CHARGE, 150, house.getId());

        mockMvc.perform(post("/api/v1/houses/" + house.getId() + "/battery/commands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}