package ro.mycode.solarsyncbroker.battery.exceptions;

import ro.mycode.solarsyncbroker.system.constants.ErrorConstants;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super(ErrorConstants.ACCESS_DENIED);
    }
}
