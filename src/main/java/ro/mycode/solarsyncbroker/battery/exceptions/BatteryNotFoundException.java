package ro.mycode.solarsyncbroker.battery.exceptions;

import ro.mycode.solarsyncbroker.system.constants.ErrorConstants;

public class BatteryNotFoundException extends RuntimeException {
    public BatteryNotFoundException() {
        super(ErrorConstants.BATTERY_NOT_FOUND);
    }
}
