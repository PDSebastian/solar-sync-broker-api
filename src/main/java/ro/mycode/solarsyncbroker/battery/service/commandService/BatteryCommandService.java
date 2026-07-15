package ro.mycode.solarsyncbroker.battery.service.commandService;

import ro.mycode.solarsyncbroker.battery.dtos.BatteryConfigurationRequest;
import ro.mycode.solarsyncbroker.battery.dtos.BatteryResponse;
import ro.mycode.solarsyncbroker.battery.model.Battery;

public interface BatteryCommandService {
    BatteryResponse updateConfiguration(Long houseId, BatteryConfigurationRequest request);
}
