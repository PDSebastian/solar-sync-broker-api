package ro.mycode.solarsyncbroker.battery.service.commandService;

import jdk.jfr.Timestamp;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ro.mycode.solarsyncbroker.battery.dtos.BatteryConfigurationRequest;
import ro.mycode.solarsyncbroker.battery.dtos.BatteryResponse;
import ro.mycode.solarsyncbroker.battery.exceptions.BatteryNotFoundException;
import ro.mycode.solarsyncbroker.battery.mapper.BatteryMapper;
import ro.mycode.solarsyncbroker.battery.model.Battery;
import ro.mycode.solarsyncbroker.battery.repository.BatteryRepository;

@Component
public class BatteryCommandServiceImpl implements BatteryCommandService {

    private BatteryRepository batteryRepository;
    public BatteryCommandServiceImpl(BatteryRepository batteryRepository) {
        this.batteryRepository = batteryRepository;
    }

    @Override
    @Transactional
    public BatteryResponse updateConfiguration(Long houseId, BatteryConfigurationRequest request) {
        Battery battery = batteryRepository.findBatteryByHouseId(houseId)
                .orElseThrow(() -> new BatteryNotFoundException());

        if (request.maxChargePowerKw() != null) {
            battery.setMaxChargePowerKw(request.maxChargePowerKw());
        }
        if (request.maxDischargePowerKw() != null) {
            battery.setMaxDischargePowerKw(request.maxDischargePowerKw());
        }

        Battery savedBattery = batteryRepository.save(battery);
        return BatteryMapper.batterytoBatteryResponse(savedBattery);
    }

}
