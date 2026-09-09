package ro.mycode.solarsyncbroker.battery.exceptions;

import ro.mycode.solarsyncbroker.system.constants.ErrorConstants;

public class AccessDeniedExceptions extends RuntimeException {
    public AccessDeniedExceptions() {
        super(ErrorConstants.ACCESS_DENIED);
    }
}
