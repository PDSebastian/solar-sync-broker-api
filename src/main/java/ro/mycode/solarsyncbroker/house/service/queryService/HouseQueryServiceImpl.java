package ro.mycode.solarsyncbroker.house.service.queryService;

import org.springframework.stereotype.Component;
import ro.mycode.solarsyncbroker.house.dtos.HouseResponse;
import ro.mycode.solarsyncbroker.house.exceptions.HouseAccessDeniedHandler;
import ro.mycode.solarsyncbroker.house.exceptions.HouseNotFoundException;
import ro.mycode.solarsyncbroker.house.mapper.HouseMapper;
import ro.mycode.solarsyncbroker.house.model.House;
import ro.mycode.solarsyncbroker.house.repository.HouseRepository;
import ro.mycode.solarsyncbroker.users.model.User;
import ro.mycode.solarsyncbroker.users.model.UserType;
import ro.mycode.solarsyncbroker.users.repository.UserRepository;

import java.util.List;

@Component
public class HouseQueryServiceImpl implements HouseQueryService {

    HouseRepository houseRepository;
    UserRepository userRepository;

    public HouseQueryServiceImpl(HouseRepository houseRepository, UserRepository userRepository) {
        this.houseRepository = houseRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<HouseResponse> getAllHouses() {
        List<House> h = houseRepository.findAll();
        return h.stream().map(HouseMapper::houseToHouseResponse).toList();
    }

    @Override
    public HouseResponse getHouseById(Long id) {
        House h = houseRepository.findById(id).orElseThrow(() -> new HouseNotFoundException());
        return HouseMapper.houseToHouseResponse(h);
    }

    @Override
    public HouseResponse getHouseForCaller(Long id, String email) {
        User caller = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException());
        House h = houseRepository.findById(id).orElseThrow(() -> new HouseNotFoundException());

        if (caller.getUserType() != UserType.ADMIN && !h.getOwner().getId().equals(caller.getId())) {
            throw new HouseAccessDeniedHandler();
        }

        return HouseMapper.houseToHouseResponse(h);
    }
}