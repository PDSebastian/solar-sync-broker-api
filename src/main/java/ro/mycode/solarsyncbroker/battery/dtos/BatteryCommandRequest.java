package ro.mycode.solarsyncbroker.battery.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ManyToAny;

public record BatteryCommandRequest(
        @NotNull(message = "Tipul comenzii nu poate fi nul")
        String commandType,

        @NotNull(message = "TargetSoc este obligatoriu")
        @Min(value = 10, message = "valoare minima pentru baterie este 10%")
        @Max(value = 90, message = "valaorea maxima pentru baterie este 90%")
        Integer targetSoc,

        @NotNull(message = "HouseId este obligatoriu")
        Long houseId
) {
}
