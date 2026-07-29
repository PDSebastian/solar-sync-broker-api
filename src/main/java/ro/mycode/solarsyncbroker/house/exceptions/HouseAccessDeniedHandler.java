package ro.mycode.solarsyncbroker.house.exceptions;

import ro.mycode.solarsyncbroker.system.constants.ErrorConstants;

public class HouseAccessDeniedHandler extends RuntimeException {
    public HouseAccessDeniedHandler() {
        super(ErrorConstants.HOUSE_ACCESS_DENIED);
    }
}
