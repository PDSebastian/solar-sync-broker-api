package ro.mycode.solarsyncbroker.energy.service;

import org.springframework.stereotype.Component;
import ro.mycode.solarsyncbroker.energy.dtos.EnergyBalanceResult;

@Component
public class EnergyEngineImpl implements EnergyEngine {

    @Override
    public EnergyBalanceResult compute(double pvPowerKw, double loadPowerKw, double batteryPowerKw, double deltaHours) {

        double netPowerKw = pvPowerKw - loadPowerKw + batteryPowerKw;
        double gridExportKw = 0.0;
        double gridImportKw = 0.0;

        if (netPowerKw > 0.0) {
            gridExportKw = netPowerKw;
        } else if (netPowerKw < 0.0) {
            gridImportKw = -netPowerKw;
        }

        double netEnergyKwh = netPowerKw * deltaHours;
        double gridExportKwh = gridExportKw * deltaHours;
        double gridImportKwh = gridImportKw * deltaHours;

        return new EnergyBalanceResult(
                netPowerKw,
                gridImportKw,
                gridExportKw,
                netEnergyKwh,
                gridImportKwh,
                gridExportKwh
        );
    }
}