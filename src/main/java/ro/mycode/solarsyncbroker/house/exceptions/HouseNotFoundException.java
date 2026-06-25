package ro.mycode.solarsyncbroker.house.exceptions;

public class HouseNotFoundException extends RuntimeException {
    public HouseNotFoundException(String message) {
        super(message);
    }
}
