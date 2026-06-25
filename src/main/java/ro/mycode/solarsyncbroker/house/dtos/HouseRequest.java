package ro.mycode.solarsyncbroker.house.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HouseRequest(
        @NotBlank
       String name,
       @NotNull
       double pvPeakPowerKw,
        @NotNull
       double maxImportPowerKw,
        @NotNull
       double maxExportPowerKw,

       Long ownerId


) {
}
