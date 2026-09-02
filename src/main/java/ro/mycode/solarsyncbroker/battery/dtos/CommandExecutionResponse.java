package ro.mycode.solarsyncbroker.battery.dtos;

import java.time.LocalDateTime;

public record CommandExecutionResponse(
        Long id,
        Long houseId,
        String commandType,
        Integer targetSoc,
        String status,
        String executedBy,
        LocalDateTime createdAt
) {}
