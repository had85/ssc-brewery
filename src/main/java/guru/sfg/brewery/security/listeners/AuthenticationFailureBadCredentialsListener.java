package guru.sfg.brewery.security.listeners;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import guru.sfg.brewery.domain.security.LoginFailure;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.LoginFailureRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFailureBadCredentialsListener {

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
	
	private final LoginFailureRepository loginFailureRepository; //da snimimo los login
	
	private final UserRepository userRepository; //da iscupamo postojeceg usera ako je korisnik
	                                             //pogodio usera, a omasio sifru
	@EventListener
	public void listen(AuthenticationFailureBadCredentialsEvent event) {
		
		log.info("Login failure: {}", event);
		
		if (event.getSource() instanceof UsernamePasswordAuthenticationToken) {
			val token = (UsernamePasswordAuthenticationToken) event.getSource();
			
			val loginFailureBuilder = LoginFailure.builder();
			
			log.info("User failed to login : {}", token.getPrincipal()); //ovde je principal string
			loginFailureBuilder.username((String) token.getPrincipal()); //jer je login neuspesan nema objekta
			
			userRepository.findByUsername((String) token.getPrincipal())
			     .ifPresent(loginFailureBuilder::user); //ako user postoji sacuvaj ga (user_id u tabeli)
			
			if (token.getDetails() instanceof WebAuthenticationDetails) {
				val details = (WebAuthenticationDetails) token.getDetails();
				log.info("Source IP : {}", details.getRemoteAddress());
				loginFailureBuilder.sourceIp(details.getRemoteAddress());
			}
			val loginFailure = loginFailureRepository.save(loginFailureBuilder.build());
			
			log.info("Login failure: {}", loginFailure);
			
			if(loginFailure.getUser() != null) {
				lockUserAccount(loginFailure.getUser());
			}
		}
	}
	
	private void lockUserAccount(User user) {
		if(loginFailureRepository //ako u zadnjih minut imamo vise od 1 neuspeli login lokujemo usera
				.findAllByUserAndCreatedDateIsAfter(user, Timestamp.valueOf(LocalDateTime.now().minusMinutes(1))).size() > 1) {
			log.info("Locking user account: {}", user);
			user.setAccountNonLocked(false);
			userRepository.save(user); //lokujemo usera ako neko pokusava brute fors napad
			                           //spring security automatski ne da korisniku da se loguje 
			                           //cak i ako ukuca dobre kredse
		}
	}

}
