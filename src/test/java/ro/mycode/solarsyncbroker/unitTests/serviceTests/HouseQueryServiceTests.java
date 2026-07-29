package ro.mycode.solarsyncbroker.unitTests.serviceTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.mycode.solarsyncbroker.house.dtos.HouseResponse;
import ro.mycode.solarsyncbroker.house.model.House;
import ro.mycode.solarsyncbroker.house.repository.HouseRepository;
import ro.mycode.solarsyncbroker.house.service.queryService.HouseQueryServiceImpl;
import ro.mycode.solarsyncbroker.users.model.User;
import ro.mycode.solarsyncbroker.users.model.UserType;
import ro.mycode.solarsyncbroker.users.repository.UserRepository;

import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class HouseQueryServiceTests {

    @Mock
    private HouseRepository houseRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    private HouseQueryServiceImpl houseQueryService;

  @Test
    void testGetAllHouses(){
      Long houseId=10L;
      String houseName="test";
      Double pvPeakPowerKw=6.5;
      Double maxImportPowerKw=10.0;
      Double maxExportPowerKw=5.5;
      String email="test@test";
      Long userId=10L;

      User user = User.builder().id(userId).email(email).build();

      House house = House.builder()
              .id(houseId)
              .name(houseName)
              .pvPeakPowerKw(pvPeakPowerKw)
              .maxImportPowerKw(maxImportPowerKw)
              .maxExportPowerKw(maxExportPowerKw)
              .owner(user)
              .enabled(true)
              .build();

      when(houseRepository.findAll()).thenReturn(List.of(house));

      List<HouseResponse> result = houseQueryService.getAllHouses();

      assertNotNull(result);
      assertEquals(1, result.size());
      assertEquals(houseName, result.getFirst().name());


  }
    @Test
    void testGetHouseById() {
        Long houseId = 10L;
        String houseName = "test";
        Double pvPeakPowerKw = 6.5;
        Double maxImportPowerKw = 10.0;
        Double maxExportPowerKw = 5.5;
        String email = "test@test";

        User user = User.builder().id(1L).email(email).build();

        House house = House.builder()
                .id(houseId)
                .name(houseName)
                .pvPeakPowerKw(pvPeakPowerKw)
                .maxImportPowerKw(maxImportPowerKw)
                .maxExportPowerKw(maxExportPowerKw)
                .enabled(true)
                .owner(user)
                .build();

        when(houseRepository.findById(houseId)).thenReturn(Optional.of(house));

        HouseResponse response = houseQueryService.getHouseById(houseId);
        assertNotNull(response);
        assertEquals(houseId, response.id());
        assertEquals(houseName, response.name());
    }
    @Test
    void testGetHouseForCaller() {
        Long userId = 1L;
        String email = "test@test.com";
        Long houseId = 10L;
        String houseName = "test-house";
        Double pvPeakPowerKw = 6.5;
        Double maxImportPowerKw = 10.0;
        Double maxExportPowerKw = 5.5;


        User user = User.builder()
                .id(userId)
                .email(email)
                .userType(UserType.USER)
                .build();

        House house = House.builder()
                .id(houseId)
                .name(houseName)
                .pvPeakPowerKw(pvPeakPowerKw)
                .maxImportPowerKw(maxImportPowerKw)
                .maxExportPowerKw(maxExportPowerKw)
                .enabled(true)
                .owner(user)
                .build();


        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(houseRepository.findById(houseId)).thenReturn(Optional.of(house));

        HouseResponse response = houseQueryService.getHouseForCaller(houseId, email);

        assertNotNull(response);
        assertEquals(houseId, response.id());
        assertEquals(houseName, response.name());
        assertEquals(userId, response.ownerId());
    }




}

