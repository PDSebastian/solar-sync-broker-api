package ro.mycode.solarsyncbroker.house.service.queryService;

import org.springframework.stereotype.Component;
import ro.mycode.solarsyncbroker.house.dtos.HouseResponse;
import ro.mycode.solarsyncbroker.house.exceptions.HouseNotFoundException;
import ro.mycode.solarsyncbroker.house.mapper.HouseMapper;
import ro.mycode.solarsyncbroker.house.model.House;
import ro.mycode.solarsyncbroker.house.repository.HouseRepository;
import ro.mycode.solarsyncbroker.users.exceptions.UserNotFoundException;
import ro.mycode.solarsyncbroker.users.model.User;
import ro.mycode.solarsyncbroker.users.repository.UserRepository;

import java.util.List;

@Component
public class HouseQueryServiceImpl implements HouseQueryService{
    private HouseRepository houseRepository;
    private UserRepository userRepository;


    @Override
    public List<HouseResponse> getAllHouses() {
      return houseRepository.findAll().stream().map(HouseMapper::houseToHouseResponse).toList();
    }

    @Override
    public HouseResponse getHouseById(Long id) {
       return houseRepository.findById(id).map(HouseMapper::houseToHouseResponse)
               .orElseThrow(()->new HouseNotFoundException());
    }

    @Override
    public HouseResponse getHouseForCaller(Long id, String email) {
        House house = houseRepository.findById(id).orElseThrow(()->new HouseNotFoundException());
        User user=userRepository.findByEmail(email).orElseThrow(()->new UserNotFoundException());

        return HouseMapper.houseToHouseResponse(house);
    }

}
