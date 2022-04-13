package sf.sec.brewery.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import sf.sec.brewery.domain.security.User;
import sf.sec.brewery.repositories.security.UserRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserUnlockService {
    private final UserRepository userRepository;

    @Scheduled(fixedRate = 5000)
    public void unlockAccount(){
        log.debug("Running Unlock Accounts");
        List<User> lockedUsers =  userRepository
                .findAllByAccountNonExpiredAndLastModifiedDateIsBefore(
                        false,
                        Timestamp.valueOf(LocalDateTime.now().minusSeconds(30))
                        );
        if(lockedUsers.size()>0){
            log.debug("Locked Accounts Found, Unlocking");
            lockedUsers.forEach(user  ->     user.setAccountNonLocked(true));
            userRepository.saveAll(lockedUsers);
        }else{
            log.debug("NO Locked Accounts Found");
        }
    }
}
