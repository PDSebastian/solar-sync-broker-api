package ro.mycode.solarsyncbroker.system.exceptions;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path


) {
    public static ApiErrorResponse of(int status, String error, String message, String path) {
        return new ApiErrorResponse(Instant.now(), status, error, message, path);

    }


}
