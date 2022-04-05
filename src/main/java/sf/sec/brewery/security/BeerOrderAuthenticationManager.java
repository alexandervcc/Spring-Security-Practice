package sf.sec.brewery.security;

import sf.sec.brewery.domain.security.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class BeerOrderAuthenticationManager {
    public boolean customerIdMatches(
            Authentication authentication,
            UUID customerId
    ){
        User authenticatedUser = (User) authentication.getPrincipal();

        log.debug(
                "Auth User customerId: "+authenticatedUser.getCustomer().getId()+" CustomerID: "+customerId);

        return authenticatedUser.getCustomer().getId().equals(customerId);
    }
}
