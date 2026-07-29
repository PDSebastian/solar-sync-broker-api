package ro.mycode.solarsyncbroker.system.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ro.mycode.solarsyncbroker.battery.exceptions.BatteryAlreadyExistsException;
import ro.mycode.solarsyncbroker.battery.exceptions.BatteryNotFoundException;
import ro.mycode.solarsyncbroker.house.exceptions.HouseAlreadyExistsExcption;
import ro.mycode.solarsyncbroker.house.exceptions.HouseNotFoundException;
import ro.mycode.solarsyncbroker.users.exceptions.UserAlreadyexistsException;
import ro.mycode.solarsyncbroker.users.exceptions.UserNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            UserNotFoundException.class,
            BatteryNotFoundException.class,
            HouseNotFoundException.class


    })
    public ResponseEntity<String> handleNotFoundExceptions(RuntimeException e) {
     ApiErrorResponse apiErrorResponse=ApiErrorResponse.builder()
             .message(e.getMessage()).status(HttpStatus.NO_CONTENT.value()) .build();
        return new ResponseEntity<>(apiErrorResponse.error(), HttpStatus.NOT_FOUND);

    }
    @ExceptionHandler({

            UserAlreadyexistsException.class,
            BatteryAlreadyExistsException.class,
            HouseAlreadyExistsExcption.class

    })
    public ResponseEntity<String> handleAlreadyExistsExceptions(RuntimeException e) {
        ApiErrorResponse apiErrorResponse=ApiErrorResponse.builder()
                .message(e.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .build();
        return new ResponseEntity<>(apiErrorResponse.error(), HttpStatus.CONFLICT);
    }






}
