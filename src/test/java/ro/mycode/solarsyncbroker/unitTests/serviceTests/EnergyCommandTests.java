package ro.mycode.solarsyncbroker.unitTests.serviceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.mycode.solarsyncbroker.energy.dtos.EnergyBalanceResult;
import ro.mycode.solarsyncbroker.energy.service.EnergyEngine;
import ro.mycode.solarsyncbroker.energy.service.EnergyEngineImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnergyCommandTests {
    private EnergyEngine engine;

    @BeforeEach
    void setup() {
        engine = new EnergyEngineImpl();
    }

    @Test
    void testExportInRetea() {
        double panouri = 5.0;
        double consum = 2.0;
        double baterie = 0.0;
        double oSecundaInOre = 1.0 / 3600.0;

        EnergyBalanceResult rezultat = engine.compute(panouri, consum, baterie, oSecundaInOre);

        assertEquals(3.0, rezultat.netPowerKw(), 0.0001);
        assertEquals(3.0, rezultat.gridExportKw(), 0.0001);
        assertEquals(0.0, rezultat.gridImportKw(), 0.0001);
        assertEquals(3.0 * oSecundaInOre, rezultat.gridExportKwh(), 0.0001);
    }

    @Test
    void testImportDinRetea() {
        double panouri = 1.0;
        double consum = 4.0;
        double baterie = 0.0;
        double oOra = 1.0;

        EnergyBalanceResult rezultat = engine.compute(panouri, consum, baterie, oOra);

        assertEquals(-3.0, rezultat.netPowerKw(), 0.0001);
        assertEquals(0.0, rezultat.gridExportKw(), 0.0001);
        assertEquals(3.0, rezultat.gridImportKw(), 0.0001);
        assertEquals(3.0, rezultat.gridImportKwh(), 0.0001);
    }

    @Test
    void testBaterieAjutaConsumul() {
        double panouri = 2.0;
        double consum = 3.0;
        double baterie = 1.0;
        double oOra = 1.0;

        EnergyBalanceResult rezultat = engine.compute(panouri, consum, baterie, oOra);

        assertEquals(0.0, rezultat.netPowerKw(), 0.0001);
        assertEquals(0.0, rezultat.gridExportKw(), 0.0001);
        assertEquals(0.0, rezultat.gridImportKw(), 0.0001);
    }
}
