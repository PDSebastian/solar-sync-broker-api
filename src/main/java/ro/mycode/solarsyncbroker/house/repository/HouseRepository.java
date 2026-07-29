package ro.mycode.solarsyncbroker.house.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.mycode.solarsyncbroker.house.model.House;

import java.util.List;
import java.util.Optional;

public interface HouseRepository extends JpaRepository<House, Long> {

    boolean existsByName(String name);
    List<House> findByOwnerId(Long ownerId);
    Optional<House> findByIdAndOwnerId(Long id, Long ownerId);
}