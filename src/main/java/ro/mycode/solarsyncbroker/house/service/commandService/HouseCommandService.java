package ro.mycode.solarsyncbroker.house.service.commandService;

import ro.mycode.solarsyncbroker.house.dtos.HouseRequest;
import ro.mycode.solarsyncbroker.house.dtos.HouseResponse;

public interface HouseCommandService {
    HouseResponse createHouse(HouseRequest houseRequest);
}
