package ro.mycode.solarsyncbroker.integrationTests.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ro.mycode.solarsyncbroker.house.dtos.HouseRequest;
import ro.mycode.solarsyncbroker.house.dtos.HouseResponse;
import ro.mycode.solarsyncbroker.house.exceptions.HouseAlreadyExistsExcption;
import ro.mycode.solarsyncbroker.house.exceptions.HouseNotFoundException;
import ro.mycode.solarsyncbroker.house.model.House;
import ro.mycode.solarsyncbroker.house.repository.HouseRepository;
import ro.mycode.solarsyncbroker.house.service.commandService.HouseCommandService;
import ro.mycode.solarsyncbroker.users.model.User;
import ro.mycode.solarsyncbroker.users.model.UserType;
import ro.mycode.solarsyncbroker.users.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class HouseCommandServiceIT {

    @Autowired
    private HouseCommandService houseCommandService;

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    void createHouse() {
        User owner = userRepository.save(User.builder()
                .firstName("Adi")
                .lastName("Pop")
                .password("pass123")
                .email("adi@test.com")
                .userType(UserType.USER)
                .build());

        HouseRequest request = new HouseRequest("Casa Noua", 5.5, 10.0, 4.5, owner.getId());

        HouseResponse response = houseCommandService.createHouse(request);

        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals("Casa Noua", response.name());
        assertTrue(houseRepository.findById(response.id()).isPresent());
    }

    @Test
    @Transactional
    void createHouseReturnsError() {
        User owner = userRepository.save(User.builder()
                .firstName("Gigi")
                .lastName("Test")
                .password("pass123")
                .email("gigi@test.com")
                .userType(UserType.USER)
                .build());

        houseRepository.save(House.builder()
                .name("Casa 2")
                .pvPeakPowerKw(5.0)
                .maxImportPowerKw(10.0)
                .maxExportPowerKw(4.5)
                .enabled(true)
                .owner(owner)
                .build());

        HouseRequest request = new HouseRequest("Casa 2", 6.0, 12.0, 5.0, owner.getId());

        assertThrows(HouseAlreadyExistsExcption.class, () -> houseCommandService.createHouse(request));
    }

    @Test
    @Transactional
    void updateHouse() {
        User owner = userRepository.save(User.builder()
                .firstName("Ion")
                .lastName("Vasile")
                .password("pass123")
                .email("ion@test.com")
                .userType(UserType.USER)
                .build());

        House saved = houseRepository.save(House.builder()
                .name("Nume Vechi")
                .pvPeakPowerKw(4.0)
                .maxImportPowerKw(9.0)
                .maxExportPowerKw(3.5)
                .enabled(true)
                .owner(owner)
                .build());

        HouseRequest request = new HouseRequest("Nume Schimbat", 6.5, 11.0, 5.5, owner.getId());

        HouseResponse response = houseCommandService.updateHouse(saved.getId(), request);

        assertNotNull(response);
        assertEquals("Nume Schimbat", response.name());
        assertEquals(6.5, response.pvPeakPowerKw());
    }

    @Test
    @Transactional
    void updateHouseReturnsError() {
        Long id = 999L;
        HouseRequest request = new HouseRequest("Casa 3", 5.0, 10.0, 4.0, 1L);

        assertThrows(HouseNotFoundException.class, () -> houseCommandService.updateHouse(id, request));
    }

    @Test
    @Transactional
    void patchHouse() {
        User owner = userRepository.save(User.builder()
                .firstName("Ema")
                .lastName("M")
                .password("pass123")
                .email("ema@test.com")
                .userType(UserType.USER)
                .build());

        House saved = houseRepository.save(House.builder()
                .name("Casa Constanta")
                .pvPeakPowerKw(4.0)
                .maxImportPowerKw(10.0)
                .maxExportPowerKw(4.0)
                .enabled(true)
                .owner(owner)
                .build());

        HouseRequest request = new HouseRequest("Casa Patch", null, null, null, null);

        HouseResponse response = houseCommandService.patchHouse(saved.getId(), request);

        assertNotNull(response);
        assertEquals("Casa Patch", response.name());
        assertEquals(4.0, response.pvPeakPowerKw());
    }

    @Test
    @Transactional
    void deleteHouse() {
        User owner = userRepository.save(User.builder()
                .firstName("Lia")
                .lastName("T")
                .password("pass123")
                .email("lia@test.com")
                .userType(UserType.USER)
                .build());

        House saved = houseRepository.save(House.builder()
                .name("Casa De Sters")
                .pvPeakPowerKw(5.0)
                .maxImportPowerKw(10.0)
                .maxExportPowerKw(5.0)
                .enabled(true)
                .owner(owner)
                .build());

        Long id = saved.getId();
        houseCommandService.deleteHouse(id);

        assertFalse(houseRepository.findById(id).isPresent());
    }

    @Test
    @Transactional
    void deleteHouseReturnsError() {
        Long id = 999L;

        assertThrows(HouseNotFoundException.class, () -> houseCommandService.deleteHouse(id));
    }
}