package ro.mycode.solarsyncbroker.battery.dtos;

public record BatteryResponse(
        Long id,
        Double socPercent,
        Double maxChargePowerKw,
        Double maxDischargePowerKw,
        Double efficiencyPercent,
        Long houseId
) {
}
