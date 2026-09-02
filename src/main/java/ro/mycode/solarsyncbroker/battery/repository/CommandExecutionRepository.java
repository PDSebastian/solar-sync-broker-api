package ro.mycode.solarsyncbroker.battery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.mycode.solarsyncbroker.battery.command.model.CommandExecution;

import java.util.List;

public interface CommandExecutionRepository extends JpaRepository<CommandExecution, Long> {
    List<CommandExecution> findByHouseId(Long houseId);
}
