package ro.mycode.solarsyncbroker.battery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.mycode.solarsyncbroker.battery.model.Battery;

import java.util.Optional;

public interface BatteryRepository extends JpaRepository<Battery, Long> {
    Optional<Battery> findBatteryByHouseId(Long id);
}
