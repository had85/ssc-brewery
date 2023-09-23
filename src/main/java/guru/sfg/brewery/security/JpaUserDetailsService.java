package guru.sfg.brewery.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//ako zelimo da spring security koristi nas nacin cupanja korisnika iz baze/externog servisa sta god
//implementiramo UserDetails service i pustimo spring autokonfiguraciju da pokupi ovu komponentu i overajduje
//difolt

@RequiredArgsConstructor
@Service
@Slf4j
public class JpaUserDetailsService implements UserDetailsService {
	
	private final UserRepository userRepository;
    //ispod haube cupamo korisnika i prepakivamo ga u spring domenski objekat koji spring security
	//zna da cita
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		log.info("logging in user: {}", username);
		
		return userRepository.findByUsername(username)
		        .orElseThrow(()-> new UsernameNotFoundException("Username: " + username + " not found."));
	}

}
