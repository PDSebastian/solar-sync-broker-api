package ro.mycode.solarsyncbroker.house.exceptions;

import ro.mycode.solarsyncbroker.system.constants.ErrorConstants;

public class HouseNotFoundException extends RuntimeException {
    public HouseNotFoundException() {
        super(ErrorConstants.HOUSE_NOT_FOUND);
    }
}
