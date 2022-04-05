package sf.sec.brewery.repositories.security;

import sf.sec.brewery.domain.security.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jt on 6/21/20.
 */
public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
}
