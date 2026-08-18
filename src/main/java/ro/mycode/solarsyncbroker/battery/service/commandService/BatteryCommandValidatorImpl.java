package ro.mycode.solarsyncbroker.battery.service.commandService;

import org.springframework.stereotype.Component;
import ro.mycode.solarsyncbroker.battery.dtos.BatteryCommand;
import ro.mycode.solarsyncbroker.battery.model.Battery;
import ro.mycode.solarsyncbroker.battery.dtos.BatteryAction;
import ro.mycode.solarsyncbroker.battery.exceptions.BatteryPowerLimitException;
import ro.mycode.solarsyncbroker.battery.exceptions.BatterySocLimitException;


@Component
public class BatteryCommandValidatorImpl implements BatteryCommandValidator {

    private static final double DEFAULT_CAPACITY_KWH = 10.0;
    private static final double MIN_SOC_PERCENT = 10.0;
    private static final double MAX_SOC_PERCENT = 90.0;

    @Override
    public void validate(Battery battery, BatteryCommand command, double deltaHours) {
        if (command == null || command.action() == BatteryAction.IDLE || command.powerKw() <= 0.0) {
            return;
        }

        if (command.action() == BatteryAction.CHARGE) {
            validateCharge(battery, command.powerKw(), deltaHours);
        } else if (command.action() == BatteryAction.DISCHARGE) {
            validateDischarge(battery, command.powerKw(), deltaHours);
        }
    }

    private void validateCharge(Battery b, double powerKw, double hours) {
        if (powerKw > b.getMaxChargePowerKw()) {
            throw new BatteryPowerLimitException();
        }

        double efficiency = b.getEfficientyPercent() / 100.0;
        double energyAdded = powerKw * hours * efficiency;
        double futureSoc = b.getSocPercent() + (energyAdded / DEFAULT_CAPACITY_KWH) * 100.0;

        if (futureSoc > MAX_SOC_PERCENT) {
            throw new BatterySocLimitException();
        }
    }

    private void validateDischarge(Battery b, double powerKw, double hours) {
        if (powerKw > b.getMaxDischargePowerKw()) {
            throw new BatteryPowerLimitException();
        }

        double efficiency = b.getEfficientyPercent() / 100.0;
        double energyRemoved = (powerKw * hours) / efficiency;
        double futureSoc = b.getSocPercent() - (energyRemoved / DEFAULT_CAPACITY_KWH) * 100.0;

        if (futureSoc < MIN_SOC_PERCENT) {
            throw new BatterySocLimitException();
        }
    }
}