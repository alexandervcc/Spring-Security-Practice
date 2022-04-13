package sf.sec.brewery.repositories.security;

import org.springframework.data.jpa.repository.JpaRepository;
import sf.sec.brewery.domain.security.LoginSucess;

public interface SucessLoginRepository extends JpaRepository<LoginSucess, Integer> {
}
