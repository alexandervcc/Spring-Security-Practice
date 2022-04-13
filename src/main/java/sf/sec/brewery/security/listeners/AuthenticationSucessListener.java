package sf.sec.brewery.security.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import sf.sec.brewery.domain.security.LoginSucess;
import sf.sec.brewery.domain.security.User;
import sf.sec.brewery.repositories.security.SucessLoginRepository;

@RequiredArgsConstructor
@Slf4j
@Component
public class AuthenticationSucessListener {

    private final SucessLoginRepository sucessLoginRepository;

    @EventListener
    public void listen(AuthenticationSuccessEvent event){
        log.debug("User LoggedIn Okay!!");

        if(event.getSource() instanceof UsernamePasswordAuthenticationToken){
            LoginSucess.LoginSucessBuilder loginSucessBuilder = LoginSucess.builder();

            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken)
                    event.getSource();
            if(token.getPrincipal() instanceof User){
                User user = (User) token.getPrincipal();
                loginSucessBuilder.user(user);

                log.debug("User name logged in: "+user.getUsername());
            }
            if(token.getDetails() instanceof WebAuthenticationDetails){
                WebAuthenticationDetails details = (WebAuthenticationDetails) token.getDetails();
                loginSucessBuilder.sourceIp(details.getRemoteAddress());

                log.debug("Source IP: "+details.getRemoteAddress());
             }
            LoginSucess loginSucess = sucessLoginRepository.save(loginSucessBuilder.build());

            log.debug("Logging Sucess Saved, Id: "+loginSucess.getId());
        }
    }
}
