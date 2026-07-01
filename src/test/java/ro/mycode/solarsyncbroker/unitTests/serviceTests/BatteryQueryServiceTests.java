package ro.mycode.solarsyncbroker.unitTests.serviceTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.mycode.solarsyncbroker.battery.dtos.BatteryResponse;
import ro.mycode.solarsyncbroker.battery.model.Battery;
import ro.mycode.solarsyncbroker.battery.repository.BatteryRepository;
import ro.mycode.solarsyncbroker.battery.service.queryService.BatteryQueryService;
import ro.mycode.solarsyncbroker.battery.service.queryService.BatteryQueryServiceImpl;
import ro.mycode.solarsyncbroker.house.model.House;
import ro.mycode.solarsyncbroker.users.model.User;
import ro.mycode.solarsyncbroker.users.model.UserType;
import ro.mycode.solarsyncbroker.users.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class BatteryQueryServiceTests {
    @Mock
    private BatteryRepository batteryRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BatteryQueryServiceImpl batteryQueryService;


    @Test
    void testGetBatteryByHouseId(){
        Long houseId = 1L;
        Long userId = 1L;
        Long batteryId = 1L;
        String email = "emailTest@gmail.com";
        User user = User.builder().id(userId).email(email).userType(UserType.ADMIN).build();
        House house=House.builder().id(houseId).build();
        Battery battery=Battery.builder()
                .id(batteryId)
                .socPercent(50.5)
                .maxChargePowerKw(10.0)
                .house(house)
                .build();


        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(batteryRepository.findBatteryByHouseId(houseId)).thenReturn(Optional.of(battery));


        BatteryResponse response=batteryQueryService.getBatteryByHouseId(houseId,email);
        assertEquals(batteryId,response.id());
        assertEquals(50.5, response.socPercent());
        assertEquals(10.0, response.maxChargePowerKw());
        assertEquals(houseId,response.houseId());

    }
}
