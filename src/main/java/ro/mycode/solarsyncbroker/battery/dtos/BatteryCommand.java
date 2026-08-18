package ro.mycode.solarsyncbroker.battery.dtos;

import jakarta.validation.constraints.NotNull;

public record BatteryCommand(
        @NotNull(message = "Battery action is required")
        BatteryAction action,

        @NotNull(message = "Power is required")
        double powerKw
) {
}
