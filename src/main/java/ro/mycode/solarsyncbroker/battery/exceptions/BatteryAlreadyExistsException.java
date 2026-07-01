package ro.mycode.solarsyncbroker.battery.exceptions;

import ro.mycode.solarsyncbroker.system.constants.ErrorConstants;

public class BatteryAlreadyExistsException extends RuntimeException {
    public BatteryAlreadyExistsException() {
        super(ErrorConstants.BATTERY_ALREADY_EXISTS);
    }
}
