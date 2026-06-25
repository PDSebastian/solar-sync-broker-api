package ro.mycode.solarsyncbroker.house.mapper;

import org.springframework.stereotype.Component;
import ro.mycode.solarsyncbroker.house.dtos.HouseResponse;
import ro.mycode.solarsyncbroker.house.model.House;

@Component
public class HouseMapper {
    public static HouseResponse houseToHouseResponse(House house) {
        if (house == null) {
            return null;
        }
        return new HouseResponse(
                house.getId(),
                house.getName(),
                house.getPvPeakPowerKw(),
                house.getMaxImportPowerKw(),
                house.getMaxExportPowerKw()
        );
    }
}
