package ro.mycode.solarsyncbroker.house.service.commandService;

import org.springframework.stereotype.Component;
import ro.mycode.solarsyncbroker.house.dtos.HouseRequest;
import ro.mycode.solarsyncbroker.house.dtos.HouseResponse;
import ro.mycode.solarsyncbroker.house.repository.HouseRepository;
import ro.mycode.solarsyncbroker.users.repository.UserRepository;

@Component
public class HouseCommandServiceImpl implements HouseCommandService {
    private HouseRepository houseRepository;
    private UserRepository userRepository;

    public HouseCommandServiceImpl(HouseRepository houseRepository, UserRepository userRepository) {
        this.houseRepository = houseRepository;
        this.userRepository = userRepository;
    }


    @Override
    public HouseResponse createHouse(HouseRequest houseRequest) {
          if(houseRepository.existsByName(houseRequest.name())){

          }
    }
}
