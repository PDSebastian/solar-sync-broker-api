package ro.mycode.solarsyncbroker.battery.service.commandService;

import org.springframework.stereotype.Service; // Recomandat în loc de @Component
import org.springframework.transaction.annotation.Transactional;
import ro.mycode.solarsyncbroker.battery.command.model.CommandExecution;
import ro.mycode.solarsyncbroker.battery.dtos.*;
import ro.mycode.solarsyncbroker.battery.exceptions.BatteryNotFoundException;
import ro.mycode.solarsyncbroker.battery.mapper.BatteryMapper;
import ro.mycode.solarsyncbroker.battery.mapper.CommandExecutionMapper;
import ro.mycode.solarsyncbroker.battery.model.Battery;
import ro.mycode.solarsyncbroker.battery.repository.BatteryRepository;
import ro.mycode.solarsyncbroker.battery.repository.CommandExecutionRepository;
import ro.mycode.solarsyncbroker.house.exceptions.HouseAccessDeniedHandler;
import ro.mycode.solarsyncbroker.house.exceptions.HouseNotFoundException;
import ro.mycode.solarsyncbroker.house.model.House;
import ro.mycode.solarsyncbroker.house.repository.HouseRepository;
import ro.mycode.solarsyncbroker.users.exceptions.UserNotFoundException;
import ro.mycode.solarsyncbroker.users.model.User;
import ro.mycode.solarsyncbroker.users.model.UserType;
import ro.mycode.solarsyncbroker.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BatteryCommandServiceImpl implements BatteryCommandService {

    private static final double DEFAULT_CAPACITY_KWH = 10.0;
    private static final double MIN_SOC_PERCENT = 10.0;
    private static final double MAX_SOC_PERCENT = 90.0;

    private final BatteryRepository batteryRepository;
    private final BatteryCommandValidator validator;
    private final CommandExecutionRepository repository;
    private final HouseRepository houseRepository;
    private final UserRepository userRepository; // Adăugat aici

    public BatteryCommandServiceImpl(BatteryRepository batteryRepository,
                                     BatteryCommandValidator validator,
                                     CommandExecutionRepository repository,
                                     HouseRepository houseRepository,
                                     UserRepository userRepository) { // Injectat în constructor
        this.batteryRepository = batteryRepository;
        this.validator = validator;
        this.repository = repository;
        this.houseRepository = houseRepository;
        this.userRepository = userRepository;
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
        User user = userRepository.findByEmail(username).orElseThrow(UserNotFoundException::new);
        House house = houseRepository.findById(houseId)
                .orElseThrow(HouseNotFoundException::new);

        if (user.getUserType() != UserType.ADMIN && !house.getOwner().getId().equals(user.getId())) {
            throw new HouseAccessDeniedHandler();
        }

        Battery battery = batteryRepository.findBatteryByHouseId(houseId)
                .orElseThrow(BatteryNotFoundException::new);

        double defaultTickHours = 1.0;
        BatteryCommand batteryCommand = new BatteryCommand(request.commandType(), request.targetSoc().doubleValue());

        String status;
        try {
            applyCommand(battery, batteryCommand, defaultTickHours);
            status = "SUCCESS";
        } catch (Exception e) {
            status = "REJECTED";
        }

        CommandExecution execution = CommandExecution.builder()
                .houseId(houseId)
                .commandType(request.commandType().name())
                .targetSoc(request.targetSoc())
                .status(status)
                .executedBy(username)
                .createdAt(LocalDateTime.now())
                .build();

        CommandExecution saved = repository.save(execution);
        return CommandExecutionMapper.commandExecutionToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandExecutionResponse> getCommandsForHouse(Long houseId, String username) {
        User user = userRepository.findByEmail(username).orElseThrow(UserNotFoundException::new);
        House house = houseRepository.findById(houseId).orElseThrow(HouseNotFoundException::new);

        if (user.getUserType() != UserType.ADMIN && !house.getOwner().getId().equals(user.getId())) {
            throw new HouseAccessDeniedHandler();
        }

        return repository.findByHouseId(houseId).stream()
                .map(CommandExecutionMapper::commandExecutionToResponse)
                .toList();
    }
}