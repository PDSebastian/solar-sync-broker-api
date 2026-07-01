package ro.mycode.solarsyncbroker.battery.service.queryService;

import org.springframework.stereotype.Component;
import ro.mycode.solarsyncbroker.battery.dtos.BatteryResponse;
import ro.mycode.solarsyncbroker.battery.mapper.BatteryMapper;
import ro.mycode.solarsyncbroker.battery.model.Battery;
import ro.mycode.solarsyncbroker.battery.repository.BatteryRepository;
import ro.mycode.solarsyncbroker.house.exceptions.HouseAccessDeniedHandler;
import ro.mycode.solarsyncbroker.house.exceptions.HouseNotFoundException;
import ro.mycode.solarsyncbroker.users.exceptions.UserNotFoundException;
import ro.mycode.solarsyncbroker.users.model.User;
import ro.mycode.solarsyncbroker.users.model.UserType;
import ro.mycode.solarsyncbroker.users.repository.UserRepository;

@Component
public class BatteryQueryServiceImpl implements BatteryQueryService {
    private BatteryRepository batteryRepository;
    private UserRepository  userRepository;

    public BatteryQueryServiceImpl(BatteryRepository batteryRepository, UserRepository userRepository) {
        this.batteryRepository = batteryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BatteryResponse getBatteryByHouseId(Long houseId, String email) {
        User user=userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        Battery battery=batteryRepository.findBatteryByHouseId(houseId).orElseThrow(HouseNotFoundException::new);


        if(user.getUserType()!=UserType.ADMIN){
            throw new HouseAccessDeniedHandler();
        }

      return BatteryMapper.batterytoBatteryResponse(battery);
    }
}
