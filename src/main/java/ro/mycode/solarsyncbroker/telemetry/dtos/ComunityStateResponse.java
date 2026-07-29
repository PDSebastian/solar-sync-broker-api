package ro.mycode.solarsyncbroker.telemetry.dtos;

public record ComunityStateResponse(
        int totalHouses,
        double totalPvPowerKw,
        double totalLoadPowerKw,
        double totalGridPowerKw,
        double averageBatterySocPercent,
        String simulationStatus,
        long currentTick
) {
}
