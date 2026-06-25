package ro.mycode.solarsyncbroker.house.exceptions;

import ro.mycode.solarsyncbroker.system.constants.ErrorConstants;

public class HouseAlreadyExistsExcption extends RuntimeException {
    public HouseAlreadyExistsExcption() {
        super(ErrorConstants.HOUSE_ALREADY_EXISTS);
    }
}
