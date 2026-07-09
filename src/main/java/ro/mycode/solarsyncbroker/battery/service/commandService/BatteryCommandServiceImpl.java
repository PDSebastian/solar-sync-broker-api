//package ro.mycode.solarsyncbroker.battery.service.commandService;
//
//import jdk.jfr.Timestamp;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//import ro.mycode.solarsyncbroker.battery.dtos.BatteryResponse;
//import ro.mycode.solarsyncbroker.battery.exceptions.BatteryNotFoundException;
//import ro.mycode.solarsyncbroker.battery.model.Battery;
//import ro.mycode.solarsyncbroker.battery.repository.BatteryRepository;
//
//@Component
//public class BatteryCommandServiceImpl implements BatteryCommandService {
//
//    private BatteryRepository batteryRepository;
//    public BatteryCommandServiceImpl(BatteryRepository batteryRepository) {
//        this.batteryRepository = batteryRepository;
//    }
//
//    @Override
//    @Transactional
//    public BatteryResponse updateConfiguration(Long houseId, Battery battery) {
//        Battery battery1= batteryRepository.findBatteryByHouseId(houseId)
//                .orElseThrow(()->new BatteryNotFoundException());
//
//
//
//
//
//    }
//}
