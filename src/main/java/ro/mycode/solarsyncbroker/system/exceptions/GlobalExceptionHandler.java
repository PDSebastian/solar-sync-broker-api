package ro.mycode.solarsyncbroker.system.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ro.mycode.solarsyncbroker.users.exceptions.UserAlreadyexistsException;
import ro.mycode.solarsyncbroker.users.exceptions.UserNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            UserNotFoundException.class


    })
    public ResponseEntity<String> handleNotFoundExceptions(RuntimeException e) {
        ApiErrorResponse apiErrorResponse=ApiErrorResponse.builder()
                .message(e.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .build();
        return new ResponseEntity<>(apiErrorResponse.toString(), HttpStatus.CONFLICT);

    }
    @ExceptionHandler({

            UserAlreadyexistsException.class

    })
    public ResponseEntity<String> handleAlreadyExistsExceptions(RuntimeException e) {
        ApiErrorResponse apiErrorResponse=ApiErrorResponse.builder()
                .message(e.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .build();
        return new ResponseEntity<>(apiErrorResponse.toString(), HttpStatus.CONFLICT);
    }






}
