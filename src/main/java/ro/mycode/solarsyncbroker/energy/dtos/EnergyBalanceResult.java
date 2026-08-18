package ro.mycode.solarsyncbroker.energy.dtos;

public record EnergyBalanceResult(
        double netPowerKw,
        double gridImportKw,
        double gridExportKw,
        double netEnergyKwh,
        double gridImportKwh,
        double gridExportKwh



) {
}
