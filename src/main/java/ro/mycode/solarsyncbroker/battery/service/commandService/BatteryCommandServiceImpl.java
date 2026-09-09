package ro.mycode.solarsyncbroker.battery.service.commandService;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ro.mycode.solarsyncbroker.battery.command.model.CommandExecution;
import ro.mycode.solarsyncbroker.battery.dtos.*;
import ro.mycode.solarsyncbroker.battery.exceptions.AccessDeniedExceptions;
import ro.mycode.solarsyncbroker.battery.exceptions.BatteryNotFoundException;
import ro.mycode.solarsyncbroker.battery.mapper.BatteryMapper;
import ro.mycode.solarsyncbroker.battery.mapper.CommandExecutionMapper;
import ro.mycode.solarsyncbroker.battery.model.Battery;
import ro.mycode.solarsyncbroker.battery.repository.BatteryRepository;
import ro.mycode.solarsyncbroker.battery.repository.CommandExecutionRepository;
import ro.mycode.solarsyncbroker.house.exceptions.HouseNotFoundException;
import ro.mycode.solarsyncbroker.house.model.House;
import ro.mycode.solarsyncbroker.house.repository.HouseRepository;
import ro.mycode.solarsyncbroker.users.exceptions.UserNotFoundException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class BatteryCommandServiceImpl implements BatteryCommandService {

    private static final double DEFAULT_CAPACITY_KWH = 10.0;
    private static final double MIN_SOC_PERCENT = 10.0;
    private static final double MAX_SOC_PERCENT = 90.0;

    private final BatteryRepository batteryRepository;
    private final BatteryCommandValidator validator;
    private final CommandExecutionRepository repository;
    private final HouseRepository houseRepository;
    private CommandExecutionRepository commandExecutionRepository;
    private CommandExecutionMapper commandExecutionMapper;

    public BatteryCommandServiceImpl(BatteryRepository batteryRepository,
                                     BatteryCommandValidator validator,
                                     CommandExecutionRepository repository,
                                     HouseRepository houseRepository,CommandExecutionMapper commandExecutionMapper) {
        this.batteryRepository = batteryRepository;
        this.validator = validator;
        this.repository = repository;
        this.houseRepository = houseRepository;
        this.commandExecutionMapper = commandExecutionMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public BatteryResponse updateConfiguration(Long houseId, BatteryConfigurationRequest request) {
        Battery battery = batteryRepository.findBatteryByHouseId(houseId)
                .orElseThrow(BatteryNotFoundException::new);

        if (request.maxChargePowerKw() != null) {
            battery.setMaxChargePowerKw(request.maxChargePowerKw());
        }
        if (request.maxDischargePowerKw() != null) {
            battery.setMaxDischargePowerKw(request.maxDischargePowerKw());
        }

        Battery savedBattery = batteryRepository.save(battery);
        return BatteryMapper.batterytoBatteryResponse(savedBattery);
    }

    @Override
    @Transactional
    public Battery applyCommand(Battery battery, BatteryCommand command, double deltaHours) {
        this.validator.validate(battery, command, deltaHours);

        if (command == null || command.action() == BatteryAction.IDLE || command.powerKw() <= 0.0) {
            return battery;
        }

        double currentSoc = battery.getSocPercent();
        double efficiency = battery.getEfficientyPercent() / 100.0;

        if (command.action() == BatteryAction.CHARGE) {
            double energyAdded = command.powerKw() * deltaHours * efficiency;
            double deltaSoc = (energyAdded / DEFAULT_CAPACITY_KWH) * 100.0;
            double newSoc = currentSoc + deltaSoc;

            if (newSoc > MAX_SOC_PERCENT) {
                newSoc = MAX_SOC_PERCENT;
            }
            battery.setSocPercent(newSoc);

        } else if (command.action() == BatteryAction.DISCHARGE) {
            double energyRemoved = (command.powerKw() * deltaHours) / efficiency;
            double deltaSoc = (energyRemoved / DEFAULT_CAPACITY_KWH) * 100.0;
            double newSoc = currentSoc - deltaSoc;

            if (newSoc < MIN_SOC_PERCENT) {
                newSoc = MIN_SOC_PERCENT;
            }
            battery.setSocPercent(newSoc);
        }

        return this.batteryRepository.save(battery);
    }

    @Override
    @Transactional
    public CommandExecutionResponse executeCommand(Long houseId, BatteryCommandRequest request, String username) {
        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> new HouseNotFoundException());

        if (!house.getOwner().getEmail().equals(username)) {
            throw new AccessDeniedExceptions();
        }

        Battery battery = batteryRepository.findBatteryByHouseId(houseId)
                .orElseThrow(BatteryNotFoundException::new);


        battery.setSocPercent(request.targetSoc().doubleValue());
        batteryRepository.save(battery);

        CommandExecution execution = CommandExecution.builder()
                .houseId(houseId)
                .commandType(request.commandType())
                .targetSoc(request.targetSoc())
                .status("SUCCESS")
                .executedBy(username)
                .createdAt(LocalDateTime.now())
                .build();

        CommandExecution saved = repository.save(execution);
        return CommandExecutionMapper.commandExecutionToResponse(saved);    }

    @Override
    public List<CommandExecutionResponse> getCommandsForHouse(Long houseId,String  username) {
        House house=houseRepository.findById(houseId).orElseThrow(HouseNotFoundException::new);

        if (!house.getOwner().getEmail().equals(username)) {
            throw new AccessDeniedExceptions();
        }

     return commandExecutionRepository.findByHouseId(houseId).stream().map(CommandExecutionMapper::commandExecutionToResponse).toList();
    }



}