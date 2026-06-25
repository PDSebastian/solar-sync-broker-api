package ro.mycode.solarsyncbroker.house.dtos;

import jakarta.validation.constraints.NotNull;

public record HouseResponse (
        Long id,
        String name,
        double pvPeakPowerKw,
        double maxImportPowerKw,
        double maxExportPowerKw



){
}
