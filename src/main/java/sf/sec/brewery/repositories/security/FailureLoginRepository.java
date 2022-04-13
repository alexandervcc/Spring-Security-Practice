package sf.sec.brewery.repositories.security;

import org.springframework.data.jpa.repository.JpaRepository;
import sf.sec.brewery.domain.security.LoginFailure;
import sf.sec.brewery.domain.security.User;

import java.sql.Timestamp;
import java.util.List;

public interface FailureLoginRepository extends JpaRepository<LoginFailure,Integer> {
    List<LoginFailure> findAllByUserAndCreatedDateIsAfter(User user, Timestamp timestamp);
}

