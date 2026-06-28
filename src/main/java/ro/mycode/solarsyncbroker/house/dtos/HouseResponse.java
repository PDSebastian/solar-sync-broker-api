package ro.mycode.solarsyncbroker.house.dtos;

public record HouseResponse(
        Long id,
        String name,
        Double pvPeakPowerKw,
        Double maxImportPowerKw,
        Double maxExportPowerKw,
        boolean enabled,
        Long ownerId
) {}