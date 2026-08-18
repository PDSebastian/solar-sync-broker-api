package ro.mycode.solarsyncbroker.battery.exceptions;

import ro.mycode.solarsyncbroker.system.constants.ErrorConstants;

public class BatterySocLimitException extends RuntimeException {
    public BatterySocLimitException() {
        super(ErrorConstants.BATTERY_SOC_LIMIT);
    }
}
