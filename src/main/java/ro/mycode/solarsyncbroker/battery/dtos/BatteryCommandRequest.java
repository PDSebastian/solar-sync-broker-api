package ro.mycode.solarsyncbroker.battery.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BatteryCommandRequest(
        @NotNull(message = "Tipul comenzii nu poate fi nul")
        BatteryAction commandType,

        @NotNull(message = "TargetSoc este obligatoriu")
        @Min(value = 0, message = "Valoarea minima pentru baterie este 0%")
        @Max(value = 100, message = "Valoarea maxima pentru baterie este 100%")
        Integer targetSoc,

        @NotNull(message = "HouseId este obligatoriu")
        Long houseId
) {
}