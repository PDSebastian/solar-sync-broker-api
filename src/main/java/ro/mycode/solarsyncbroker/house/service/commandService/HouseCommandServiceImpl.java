package ro.mycode.solarsyncbroker.house.service.commandService;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ro.mycode.solarsyncbroker.house.dtos.HouseRequest;
import ro.mycode.solarsyncbroker.house.dtos.HouseResponse;
import ro.mycode.solarsyncbroker.house.exceptions.HouseAlreadyExistsExcption;
import ro.mycode.solarsyncbroker.house.exceptions.HouseNotFoundException;
import ro.mycode.solarsyncbroker.house.mapper.HouseMapper;
import ro.mycode.solarsyncbroker.house.model.House;
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
    @Transactional
    public HouseResponse createHouse(HouseRequest houseRequest) {
          if(houseRepository.existsByName(houseRequest.name())){
            throw new HouseAlreadyExistsExcption();
          }
          House house= House.builder()
                  .name(houseRequest.name())
                  .pvPeakPowerKw(houseRequest.pvPeakPowerKw())
                  .maxImportPowerKw(houseRequest.maxImportPowerKw())
                  .maxExportPowerKw(houseRequest.maxExportPowerKw())
                  .enabled(true)
                  .build();

          return HouseMapper.houseToHouseResponse(houseRepository.save(house));
    }

    @Override
    @Transactional
    public HouseResponse updateHouse(Long id, HouseRequest houseRequest) {
        House house =houseRepository.findById(id).orElseThrow(()->new HouseNotFoundException());
        house.setName(houseRequest.name());
        house.setPvPeakPowerKw(houseRequest.pvPeakPowerKw());
        house.setMaxImportPowerKw(houseRequest.maxImportPowerKw());
        house.setMaxExportPowerKw(houseRequest.maxExportPowerKw());


        houseRepository.save(house);
        return HouseMapper.houseToHouseResponse(house);
    }

    @Override
    @Transactional
    public HouseResponse patchHouse(Long id, HouseRequest houseRequest) {
      House house=houseRepository.findById(id).orElseThrow(()->new HouseNotFoundException());


      return HouseMapper.houseToHouseResponse(houseRepository.save(house));
    }

    @Override
    @Transactional
    public void deleteHouse(Long id) {
        if(!houseRepository.existsById(id)){
            throw new HouseNotFoundException();
        }
        houseRepository.deleteById(id);
    }

}
