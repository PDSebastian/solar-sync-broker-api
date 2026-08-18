package ro.mycode.solarsyncbroker.battery.exceptions;

import ro.mycode.solarsyncbroker.system.constants.ErrorConstants;

public class BatteryPowerLimitException extends RuntimeException {
    public BatteryPowerLimitException() {
        super(ErrorConstants.BATTERY_POWER_LIMIT);
    }
}
