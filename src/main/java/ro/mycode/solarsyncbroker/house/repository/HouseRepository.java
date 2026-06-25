package ro.mycode.solarsyncbroker.house.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.mycode.solarsyncbroker.house.model.House;

import java.util.Optional;

public interface HouseRepository extends JpaRepository<House,Long> {
    boolean existsByName(String name);
    Optional<House> findByOwnerId(Long ownerId);
    Optional<House>findIdAndOwnerId(Long ownerId,Long id);


}
