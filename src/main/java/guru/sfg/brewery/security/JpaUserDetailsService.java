package guru.sfg.brewery.security;

import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import guru.sfg.brewery.domain.security.Authority;
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
		        .map(user -> User.builder()
		    		         .username(user.getUsername())
		    		         .password(user.getPassword())
		    		         //kad radimo getAuthorities spring po difoltu lazy load-uje
		    		         //a posto krovna metoda nije @Transactional pokusavamo da koristimo hibernate
		    		         //van sesije i desava se greska
		    		         //resenje 1. krovna metoda mora biti @Transactional
		    		         //resenje 2. raditi eager fetch (anotirati entitet)
		    		         .authorities(user.getAuthorities()//izvlacimo sve granularne authoritije 
		    		        		                           //takodje mogu da budu role
		    		    		           .stream()
		    		    		           .map(Authority::getPermission)
		    		    		           .map(SimpleGrantedAuthority::new)
		    		    		           .collect(Collectors.toSet()))
		    		         .accountExpired(!user.isAccountNonExpired())
		    		         .accountLocked(!user.isAccountNonLocked())
		    		         .disabled(!user.isEnabled())
		    		         .credentialsExpired(!user.isCredentialsNonExpired())
		    		         .build())
		        .orElseThrow(()-> new UsernameNotFoundException("Username: " + username + " not found."));
	}

}
