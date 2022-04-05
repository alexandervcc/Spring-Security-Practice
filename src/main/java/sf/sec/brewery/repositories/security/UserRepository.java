package sf.sec.brewery.repositories.security;

import sf.sec.brewery.domain.security.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by jt on 6/21/20.
 */
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
}
