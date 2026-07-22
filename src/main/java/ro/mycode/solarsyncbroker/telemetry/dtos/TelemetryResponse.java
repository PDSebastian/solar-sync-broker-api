package ro.mycode.solarsyncbroker.telemetry.dtos;

import java.util.List;

public record TelemetryResponse(
        List<TelemetryResponse> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {
}
