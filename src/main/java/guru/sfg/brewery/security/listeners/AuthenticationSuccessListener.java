package guru.sfg.brewery.security.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import guru.sfg.brewery.domain.security.LoginSuccess;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.LoginSuccessRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationSuccessListener {
	
	private final LoginSuccessRepository loginSuccessRepository;

	/**
	 * AuthenticationSuccessEvent u lancu nasledjivanja nasledjuje ApplicationEvent gde je source tog eventa
	 * Object, sto znaci da moramo da ispitijujemo sa instanceof i da kastujemo
	 * AuthenticationSuccessEvent kao source ima Authentication object u ovom slucaju 
	 * (UsernamePasswordAuthenticationToken) koji ima sve podatke o ulogovanom korisniku
	 * izvrsavanje ovog eventa je sinhrono, breakpoint ovde zaustavlja ceo login
	 * principal - nas {@link User} objekaat
	 * details - remoteIP, sessionId
	 * authorities - role
	 * authoenticated - boolean
	 * @param event
	 */
	@EventListener
	public void listen(AuthenticationSuccessEvent event) {
		
		log.info("Login success: {}", event);
		
		if (event.getSource() instanceof UsernamePasswordAuthenticationToken) {
			val loginSuccessBuilder = LoginSuccess.builder();
			val token = (UsernamePasswordAuthenticationToken) event.getSource();
			
			if (token.getPrincipal() instanceof User) {
				val user = (User) token.getPrincipal();
				loginSuccessBuilder.user(user);
				log.info("User logged in : {}", user.getUsername());
			}
			
			if (token.getDetails() instanceof WebAuthenticationDetails) {
				val details = (WebAuthenticationDetails) token.getDetails();
				loginSuccessBuilder.sourceIp(details.getRemoteAddress());
				log.info("Source IP : {}", details.getRemoteAddress());
			}
			val loginSuccess = loginSuccessRepository.save(loginSuccessBuilder.build());
			
			log.info("Login success: {}", loginSuccess);
			
		}
}

}
