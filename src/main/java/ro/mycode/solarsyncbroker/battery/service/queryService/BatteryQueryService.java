package ro.mycode.solarsyncbroker.battery.service.queryService;

import ro.mycode.solarsyncbroker.battery.dtos.BatteryResponse;

public interface BatteryQueryService {
    BatteryResponse getBatteryByHouseId(Long houseId,String email);

}
