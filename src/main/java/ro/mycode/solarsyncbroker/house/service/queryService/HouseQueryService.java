package ro.mycode.solarsyncbroker.house.service.queryService;

import ro.mycode.solarsyncbroker.house.dtos.HouseResponse;

import java.util.List;

public interface HouseQueryService {
    List<HouseResponse> getAllHouses();
    HouseResponse getHouseById(Long id);
    HouseResponse getHouseForCaller(Long id,String email);



}
