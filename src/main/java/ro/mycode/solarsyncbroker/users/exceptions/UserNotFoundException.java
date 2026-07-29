package ro.mycode.solarsyncbroker.users.exceptions;

import ro.mycode.solarsyncbroker.system.constants.ErrorConstants;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super(ErrorConstants.USER_NOT_FOUND);
    }
}
