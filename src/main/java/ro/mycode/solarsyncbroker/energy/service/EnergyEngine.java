package ro.mycode.solarsyncbroker.energy.service;

import ro.mycode.solarsyncbroker.energy.dtos.EnergyBalanceResult;

public interface EnergyEngine {
    EnergyBalanceResult compute(double pvPowerKw, double loadPowerKw, double batteryPowerKw, double deltaHours);
}
