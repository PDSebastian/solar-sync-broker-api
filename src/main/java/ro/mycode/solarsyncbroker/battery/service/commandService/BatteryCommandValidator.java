package ro.mycode.solarsyncbroker.battery.service.commandService;

import ro.mycode.solarsyncbroker.battery.dtos.BatteryAction;
import ro.mycode.solarsyncbroker.battery.dtos.BatteryCommand;
import ro.mycode.solarsyncbroker.battery.model.Battery;

public interface BatteryCommandValidator {
    void validate(Battery battery, BatteryCommand command, double deltaHours);
}
