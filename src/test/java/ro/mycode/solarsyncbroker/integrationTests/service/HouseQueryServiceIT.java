package ro.mycode.solarsyncbroker.integrationTests.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ro.mycode.solarsyncbroker.house.dtos.HouseResponse;
import ro.mycode.solarsyncbroker.house.exceptions.HouseAccessDeniedHandler;
import ro.mycode.solarsyncbroker.house.exceptions.HouseNotFoundException;
import ro.mycode.solarsyncbroker.house.model.House;
import ro.mycode.solarsyncbroker.house.repository.HouseRepository;
import ro.mycode.solarsyncbroker.house.service.queryService.HouseQueryService;
import ro.mycode.solarsyncbroker.users.model.User;
import ro.mycode.solarsyncbroker.users.model.UserType;
import ro.mycode.solarsyncbroker.users.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class HouseQueryServiceIT {

    @Autowired
    private HouseQueryService houseQueryService;

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    void getAllHouses() {
        User owner = userRepository.save(User.builder()
                .firstName("Dan")
                .lastName("Test")
                .password("secret123")
                .email("dan@test.com")
                .userType(UserType.USER)
                .build());

        houseRepository.save(House.builder()
                .name("Casa Dan")
                .pvPeakPowerKw(5.0)
                .maxImportPowerKw(10.0)
                .maxExportPowerKw(4.5)
                .enabled(true)
                .owner(owner)
                .build());

        List<HouseResponse> houses = houseQueryService.getAllHouses();

        assertNotNull(houses);
        assertFalse(houses.isEmpty());
    }

    @Test
    @Transactional
    void getHouseById() {
        User owner = userRepository.save(User.builder()
                .firstName("Ana")
                .lastName("Test")
                .password("secret123")
                .email("ana@test.com")
                .userType(UserType.USER)
                .build());

        House saved = houseRepository.save(House.builder()
                .name("Casa Ana")
                .pvPeakPowerKw(6.0)
                .maxImportPowerKw(10.0)
                .maxExportPowerKw(5.0)
                .enabled(true)
                .owner(owner)
                .build());

        Long id = saved.getId();
        HouseResponse response = houseQueryService.getHouseById(id);

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals("Casa Ana", response.name());
    }

    @Test
    @Transactional
    void getHouseByIdReturnsError() {
        Long id = 999L;

        assertThrows(HouseNotFoundException.class, () -> houseQueryService.getHouseById(id));
    }

    @Test
    @Transactional
    void getHouseForCaller() {
        String email = "owner@test.com";
        User owner = userRepository.save(User.builder()
                .firstName("Owner")
                .lastName("Test")
                .password("secret123")
                .email(email)
                .userType(UserType.USER)
                .build());

        House saved = houseRepository.save(House.builder()
                .name("Casa Proprietar")
                .pvPeakPowerKw(4.5)
                .maxImportPowerKw(9.0)
                .maxExportPowerKw(4.0)
                .enabled(true)
                .owner(owner)
                .build());

        Long id = saved.getId();
        HouseResponse response = houseQueryService.getHouseForCaller(id, email);

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals(owner.getId(), response.ownerId());
    }

    @Test
    @Transactional
    void getHouseForCallerAdmin() {
        String adminEmail = "admin@test.com";
        User owner = userRepository.save(User.builder()
                .firstName("User")
                .lastName("Test")
                .password("secret123")
                .email("user@test.com")
                .userType(UserType.USER)
                .build());

        User admin = userRepository.save(User.builder()
                .firstName("Admin")
                .lastName("Test")
                .password("secret123")
                .email(adminEmail)
                .userType(UserType.ADMIN)
                .build());

        House saved = houseRepository.save(House.builder()
                .name("Casa Straina")
                .pvPeakPowerKw(4.5)
                .maxImportPowerKw(9.0)
                .maxExportPowerKw(4.0)
                .enabled(true)
                .owner(owner)
                .build());

        Long id = saved.getId();
        HouseResponse response = houseQueryService.getHouseForCaller(id, adminEmail);

        assertNotNull(response);
        assertEquals("Casa Straina", response.name());
    }

    @Test
    @Transactional
    void getHouseForCallerReturnsError() {
        String uEmail = "uEmail@test.com";
        User owner = userRepository.save(User.builder()
                .firstName("OwnerTwo")
                .lastName("Test")
                .password("secret123")
                .email("owner2@test.com")
                .userType(UserType.USER)
                .build());

        User user = userRepository.save(User.builder()
                .firstName("Stranger")
                .lastName("Test")
                .password("secret123")
                .email(uEmail)
                .userType(UserType.USER)
                .build());

        House saved = houseRepository.save(House.builder()
                .name("Casa Secreta")
                .pvPeakPowerKw(5.0)
                .maxImportPowerKw(10.0)
                .maxExportPowerKw(5.0)
                .enabled(true)
                .owner(owner)
                .build());

        Long id = saved.getId();

        assertThrows(HouseAccessDeniedHandler.class, () -> houseQueryService.getHouseForCaller(id, uEmail ));
    }
}