package ro.mycode.solarsyncbroker.telemetry.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.mycode.solarsyncbroker.telemetry.model.Telemetry;

@Repository
public interface TelemetryRepository extends JpaRepository<Telemetry, Long> {
    Page<Telemetry>findByHouseIdOrderByRecordedAtDesc(Long houseId, Pageable pageable);




}





