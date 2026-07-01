package ro.mycode.solarsyncbroker.unitTests.serviceTests;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.mycode.solarsyncbroker.house.dtos.HouseRequest;
import ro.mycode.solarsyncbroker.house.dtos.HouseResponse;
import ro.mycode.solarsyncbroker.house.exceptions.HouseAlreadyExistsExcption;
import ro.mycode.solarsyncbroker.house.exceptions.HouseNotFoundException;
import ro.mycode.solarsyncbroker.house.model.House;
import ro.mycode.solarsyncbroker.house.repository.HouseRepository;
import ro.mycode.solarsyncbroker.house.service.commandService.HouseCommandServiceImpl;
import ro.mycode.solarsyncbroker.users.exceptions.UserNotFoundException;
import ro.mycode.solarsyncbroker.users.model.User;
import ro.mycode.solarsyncbroker.users.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HouseCommandServiceImplTests {

    @Mock
    HouseRepository houseRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    HouseCommandServiceImpl houseCommandService;

    @Test
    void testCreateHouseSuccess() {
        Long userId = 1L;
        Long houseId = 10L;
        String houseName = "Casa Verde Pro";
        Double pvPower = 5.5;
        Double maxImport = 10.0;
        Double maxExport = 4.5;

        HouseRequest request = new HouseRequest(houseName, pvPower, maxImport, maxExport, userId);
        User owner = User.builder().id(userId).email("owner@solarsync.ro").build();

        when(houseRepository.existsByName(houseName)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(houseRepository.save(any(House.class))).thenAnswer(invocation -> {
            House h = invocation.getArgument(0);
            h.setId(houseId);
            return h;
        });

        HouseResponse response = houseCommandService.createHouse(request);

        assertNotNull(response);
        assertEquals(houseId, response.id());
        assertEquals(houseName, response.name());
    }

    @Test
    void testCreateHouseThrowsHouseAlreadyExistsException() {
        Long userId = 1L;
        String houseName = "Casa Existenta";
        Double pvPower = 5.5;
        Double maxImport = 10.0;
        Double maxExport = 4.5;

        HouseRequest request = new HouseRequest(houseName, pvPower, maxImport, maxExport, userId);

        when(houseRepository.existsByName(houseName)).thenReturn(true);

        assertThrows(HouseAlreadyExistsExcption.class, () -> houseCommandService.createHouse(request));
    }

    @Test
    void testCreateHouseThrowsUserNotFoundException() {
        Long userId = 99L;
        String houseName = "Casa Fara Owner";
        Double pvPower = 5.5;
        Double maxImport = 10.0;
        Double maxExport = 4.5;

        HouseRequest request = new HouseRequest(houseName, pvPower, maxImport, maxExport, userId);

        when(houseRepository.existsByName(houseName)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> houseCommandService.createHouse(request));
    }

    @Test
    void testUpdateHouseSuccess() {
        Long userId = 1L;
        Long houseId = 10L;
        String originalName = "Nume Vechi";
        String updatedName = "Nume Nou";
        Double pvPower = 7.0;
        Double maxImport = 12.0;
        Double maxExport = 6.0;

        HouseRequest request = new HouseRequest(updatedName, pvPower, maxImport, maxExport, userId);
        User owner = User.builder().id(userId).build();
        House existingHouse = House.builder().id(houseId).name(originalName).owner(owner).build();

        when(houseRepository.findById(houseId)).thenReturn(Optional.of(existingHouse));
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(houseRepository.save(any(House.class))).thenReturn(existingHouse);

        HouseResponse response = houseCommandService.updateHouse(houseId, request);

        assertNotNull(response);
        assertEquals(updatedName, existingHouse.getName());
        assertEquals(pvPower, existingHouse.getPvPeakPowerKw());
        assertEquals(updatedName, response.name());
    }

    @Test
    void testUpdateHouseThrowsHouseNotFoundException() {
        Long houseId = 999L;
        Long userId = 1L;
        HouseRequest request = new HouseRequest("Test", 5.0, 10.0, 5.0, userId);

        when(houseRepository.findById(houseId)).thenReturn(Optional.empty());

        assertThrows(HouseNotFoundException.class, () -> houseCommandService.updateHouse(houseId, request));
    }

    @Test
    void testPatchHouseUpdatesOnlyProvidedFields() {
        Long userId = 1L;
        Long houseId = 10L;
        String originalName = "Casa Constanta";
        String patchedName = "Casa Modificata";
        Double originalPvPower = 4.0;
        Double originalMaxImport = 10.0;
        Double originalMaxExport = 4.0;

        HouseRequest patchRequest = new HouseRequest(patchedName, null, null, null, null);
        User owner = User.builder().id(userId).build();
        House existingHouse = House.builder()
                .id(houseId)
                .name(originalName)
                .pvPeakPowerKw(originalPvPower)
                .maxImportPowerKw(originalMaxImport)
                .maxExportPowerKw(originalMaxExport)
                .owner(owner)
                .build();

        when(houseRepository.findById(houseId)).thenReturn(Optional.of(existingHouse));
        when(houseRepository.save(any(House.class))).thenReturn(existingHouse);

        HouseResponse response = houseCommandService.patchHouse(houseId, patchRequest);

        assertNotNull(response);
        assertEquals(patchedName, existingHouse.getName());
        assertEquals(originalPvPower, existingHouse.getPvPeakPowerKw());
        assertEquals(patchedName, response.name());
    }

    @Test
    void testDeleteHouseSuccess() {
        Long houseId = 10L;

        when(houseRepository.existsById(houseId)).thenReturn(true);

        assertDoesNotThrow(() -> houseCommandService.deleteHouse(houseId));
    }

    @Test
    void testDeleteHouseThrowsHouseNotFoundException() {
        Long houseId = 999L;

        when(houseRepository.existsById(houseId)).thenReturn(false);

        assertThrows(HouseNotFoundException.class, () -> houseCommandService.deleteHouse(houseId));
    }
}