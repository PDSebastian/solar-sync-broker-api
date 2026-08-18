package ro.mycode.solarsyncbroker.unitTests.serviceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.mycode.solarsyncbroker.battery.dtos.BatteryCommand;
import ro.mycode.solarsyncbroker.battery.model.Battery;
import ro.mycode.solarsyncbroker.battery.dtos.BatteryAction;
import ro.mycode.solarsyncbroker.battery.exceptions.BatteryPowerLimitException;
import ro.mycode.solarsyncbroker.battery.exceptions.BatterySocLimitException;
import ro.mycode.solarsyncbroker.battery.service.commandService.BatteryCommandValidator;
import ro.mycode.solarsyncbroker.battery.service.commandService.BatteryCommandValidatorImpl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BatteryCommandValidatorTests {

    private BatteryCommandValidator validator;
    private Battery baterie;

    @BeforeEach
    void setup() {
        validator = new BatteryCommandValidatorImpl();
        baterie = Battery.builder()
                .id(1L)
                .socPercent(50.0)
                .maxChargePowerKw(3.0)
                .maxDischargePowerKw(3.0)
                .efficientyPercent(95.0)
                .build();
    }

    @Test
    void testPuterePreaMareLaIncarcare() {
        BatteryCommand comanda = new BatteryCommand(BatteryAction.CHARGE, 5.0);

        assertThrows(BatteryPowerLimitException.class, () -> validator.validate(baterie, comanda, 1.0));
    }

    @Test
    void testPuterePreaMareLaDescarcare() {
        BatteryCommand comanda = new BatteryCommand(BatteryAction.DISCHARGE, 4.5);

        assertThrows(BatteryPowerLimitException.class, () -> validator.validate(baterie, comanda, 1.0));
    }

    @Test
    void testBaterieAproapeGoala() {
        baterie.setSocPercent(11.0);
        BatteryCommand comanda = new BatteryCommand(BatteryAction.DISCHARGE, 2.0);

        assertThrows(BatterySocLimitException.class, () -> validator.validate(baterie, comanda, 1.0));
    }

    @Test
    void testComandaCorecta() {
        BatteryCommand comanda = new BatteryCommand(BatteryAction.CHARGE, 2.0);

        assertDoesNotThrow(() -> validator.validate(baterie, comanda, 0.01));
    }
}