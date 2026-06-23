package ro.mycode.solarsyncbroker.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.mycode.solarsyncbroker.users.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);




}
