package ro.mycode.solarsyncbroker.house.service.commandService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.mycode.solarsyncbroker.house.dtos.HouseRequest;
import ro.mycode.solarsyncbroker.house.dtos.HouseResponse;
import ro.mycode.solarsyncbroker.house.exceptions.HouseAlreadyExistsExcption;
import ro.mycode.solarsyncbroker.house.exceptions.HouseNotFoundException;
import ro.mycode.solarsyncbroker.house.mapper.HouseMapper;
import ro.mycode.solarsyncbroker.house.model.House;
import ro.mycode.solarsyncbroker.house.repository.HouseRepository;
import ro.mycode.solarsyncbroker.users.exceptions.UserNotFoundException;
import ro.mycode.solarsyncbroker.users.model.User;
import ro.mycode.solarsyncbroker.users.repository.UserRepository;

@Service // Schimbat din @Component în @Service
public class HouseCommandServiceImpl implements HouseCommandService {

    private final HouseRepository houseRepository;
    private final UserRepository userRepository;

    public HouseCommandServiceImpl(HouseRepository houseRepository, UserRepository userRepository) {
        this.houseRepository = houseRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public HouseResponse createHouse(HouseRequest houseRequest) {
        if (houseRepository.existsByName(houseRequest.name())) {
            throw new HouseAlreadyExistsExcption();
        }

        User owner = userRepository.findById(houseRequest.ownerId())
                .orElseThrow(() -> new UserNotFoundException());

        House house = House.builder()
                .name(houseRequest.name())
                .pvPeakPowerKw(houseRequest.pvPeakPowerKw())
                .maxImportPowerKw(houseRequest.maxImportPowerKw())
                .maxExportPowerKw(houseRequest.maxExportPowerKw())
                .enabled(true)
                .owner(owner)
                .build();

        return HouseMapper.houseToHouseResponse(houseRepository.save(house));
    }

    @Override
    @Transactional
    public HouseResponse updateHouse(Long id, HouseRequest houseRequest) {
        House house = houseRepository.findById(id).orElseThrow(HouseNotFoundException::new);

        // În caz că se schimbă și owner-ul la un update total
        User owner = userRepository.findById(houseRequest.ownerId())
                .orElseThrow(() -> new UserNotFoundException());

        if(!house.getName().equals(houseRequest.name()) && houseRepository.existsByName(houseRequest.name())) {
            throw new HouseAlreadyExistsExcption();
        }

        house.setName(houseRequest.name());
        house.setPvPeakPowerKw(houseRequest.pvPeakPowerKw());
        house.setMaxImportPowerKw(houseRequest.maxImportPowerKw());
        house.setMaxExportPowerKw(houseRequest.maxExportPowerKw());
        house.setOwner(owner);

        return HouseMapper.houseToHouseResponse(houseRepository.save(house));
    }

    @Override
    @Transactional
    public HouseResponse patchHouse(Long id, HouseRequest houseRequest) {
        House house = houseRepository.findById(id).orElseThrow(HouseNotFoundException::new);

        if (houseRequest.name() != null && !houseRequest.name().isBlank()) {
            house.setName(houseRequest.name());
        }
        if (houseRequest.pvPeakPowerKw() != null) {
            house.setPvPeakPowerKw(houseRequest.pvPeakPowerKw());
        }
        if (houseRequest.maxImportPowerKw() != null) {
            house.setMaxImportPowerKw(houseRequest.maxImportPowerKw());
        }
        if (houseRequest.maxExportPowerKw() != null) {
            house.setMaxExportPowerKw(houseRequest.maxExportPowerKw());
        }
        if (houseRequest.ownerId() != null) {
            User owner = userRepository.findById(houseRequest.ownerId())
                    .orElseThrow(() -> new UserNotFoundException());
            house.setOwner(owner);
        }

        return HouseMapper.houseToHouseResponse(houseRepository.save(house));
    }

    @Override
    @Transactional
    public void deleteHouse(Long id) {
        if (!houseRepository.existsById(id)) {
            throw new HouseNotFoundException();
        }
        houseRepository.deleteById(id);
    }
}