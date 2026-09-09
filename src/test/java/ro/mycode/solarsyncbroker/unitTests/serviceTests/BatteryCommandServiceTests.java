package ro.mycode.solarsyncbroker.unitTests.serviceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.mycode.solarsyncbroker.battery.dtos.BatteryCommand;
import ro.mycode.solarsyncbroker.battery.model.Battery;
import ro.mycode.solarsyncbroker.battery.repository.BatteryRepository;
import ro.mycode.solarsyncbroker.battery.repository.CommandExecutionRepository;
import ro.mycode.solarsyncbroker.battery.service.commandService.BatteryCommandService;
import ro.mycode.solarsyncbroker.battery.service.commandService.BatteryCommandServiceImpl;
import ro.mycode.solarsyncbroker.battery.dtos.BatteryAction;
import ro.mycode.solarsyncbroker.battery.service.commandService.BatteryCommandValidator;
import ro.mycode.solarsyncbroker.house.repository.HouseRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BatteryCommandServiceTests {

    @Mock
    private BatteryRepository repository;

    @Mock
    private BatteryCommandValidator validator;

    @InjectMocks
    private BatteryCommandServiceImpl service;
    private Battery baterie;
    private BatteryCommandValidator batteryValidator;
    private  HouseRepository houseRepository;
    private CommandExecutionRepository commandExecutionRepository;


    @BeforeEach
    void setup() {
        service = new BatteryCommandServiceImpl(repository, validator,commandExecutionRepository,houseRepository);
        baterie = Battery.builder()
                .id(1L)
                .socPercent(50.0)
                .maxChargePowerKw(3.0)
                .maxDischargePowerKw(3.0)
                .efficientyPercent(100.0)
                .build();
    }

    @Test
    void testIncarcareReusita() {
       BatteryCommand comanda = new BatteryCommand(BatteryAction.CHARGE, 2.0);
        double oOra = 1.0;

        when(repository.save(any(Battery.class))).thenAnswer(i -> i.getArgument(0));

        Battery salvata = service.applyCommand(baterie, comanda, oOra);

        assertEquals(70.0, salvata.getSocPercent(), 0.0001);
        verify(validator).validate(baterie, comanda, oOra);
        verify(repository).save(baterie);
    }
}