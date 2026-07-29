package ro.mycode.solarsyncbroker.battery.dtos;

import jakarta.validation.constraints.NotNull;

public record BatteryConfigurationRequest(

        @NotNull(message = "Puterea maxima de incarcare este obligatorie")
        Double maxChargePowerKw,
        @NotNull(message = "Puterea maxima de descarcare este obligatorie")
        Double maxDischargePowerKw



) {
}
