package ro.mycode.solarsyncbroker.battery.service.commandService;

import ro.mycode.solarsyncbroker.battery.dtos.*;
import ro.mycode.solarsyncbroker.battery.model.Battery;

import java.util.List;

public interface BatteryCommandService {
    BatteryResponse updateConfiguration(Long houseId, BatteryConfigurationRequest request);
    Battery  applyCommand(Battery battery, BatteryCommand command, double deltaHours);
    CommandExecutionResponse executeCommand(Long houseId, BatteryCommandRequest request, String username);
    List<CommandExecutionResponse> getCommandsForHouse(Long houseId);

}
