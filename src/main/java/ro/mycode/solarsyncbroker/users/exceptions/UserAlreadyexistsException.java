package ro.mycode.solarsyncbroker.users.exceptions;

import ro.mycode.solarsyncbroker.system.constants.ErrorConstants;

public class UserAlreadyexistsException extends RuntimeException {
    public UserAlreadyexistsException() {
        super(ErrorConstants.USER_ALREADY_EXISTS);
    }
}
