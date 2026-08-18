package ro.mycode.solarsyncbroker.battery.service.commandService;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ro.mycode.solarsyncbroker.battery.dtos.BatteryAction;
import ro.mycode.solarsyncbroker.battery.dtos.BatteryCommand;
import ro.mycode.solarsyncbroker.battery.dtos.BatteryConfigurationRequest;
import ro.mycode.solarsyncbroker.battery.dtos.BatteryResponse;
import ro.mycode.solarsyncbroker.battery.exceptions.BatteryNotFoundException;
import ro.mycode.solarsyncbroker.battery.mapper.BatteryMapper;
import ro.mycode.solarsyncbroker.battery.model.Battery;
import ro.mycode.solarsyncbroker.battery.repository.BatteryRepository;

@Component
public class BatteryCommandServiceImpl implements BatteryCommandService {

    private static final double DEFAULT_CAPACITY_KWH = 10.0;
    private static final double MIN_SOC_PERCENT = 10.0;
    private static final double MAX_SOC_PERCENT = 90.0;

    private final BatteryRepository batteryRepository;
    private final BatteryCommandValidator validator;

    public BatteryCommandServiceImpl(BatteryRepository batteryRepository, BatteryCommandValidator validator) {
        this.batteryRepository = batteryRepository;
        this.validator = validator;
    }

    @Override
    @Transactional
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
}