package ro.mycode.solarsyncbroker.battery.mapper;

import ro.mycode.solarsyncbroker.battery.dtos.BatteryResponse;
import ro.mycode.solarsyncbroker.battery.model.Battery;

public class BatteryMapper {
    public static BatteryResponse batterytoBatteryResponse(Battery battery){
        if(battery==null){
            return null;
        }
        return new BatteryResponse(
                battery.getId(),
                battery.getSocPercent(),
                battery.getMaxChargePowerKw(),
                battery.getMaxDischargePowerKw(),
                battery.getEfficientyPercent(),
                battery.getHouse().getId()
        );
    }

}
