package guru.sfg.brewery.bootstrap;

import java.util.Arrays;

import javax.transaction.Transactional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDataLoader implements CommandLineRunner {
	
	private final AuthorityRepository authorityRepository;
	
	private final UserRepository userRepository;
	
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public void run(String... args) throws Exception {
		
		if(userRepository.count() == 0) {
			val adminRole = Authority.builder()
					         .role("ADMIN")
					         .build();
			
			val userRole = Authority.builder()
			         .role("USER")
			         .build();
			
			val customerRole = Authority.builder()
			         .role("CUSTOMER")
			         .build();
			
			authorityRepository.saveAll(Arrays.asList(adminRole, userRole, customerRole));
			
			val springUser = User.builder()
						      .username("spring")
						      .password(passwordEncoder.encode("guru"))
						      .authority(adminRole)
						      .build();
			
			val user = User.builder()
				      .username("user")
				      .password(passwordEncoder.encode("password"))
				      .authority(userRole)
				      .build();
			
			val scottUser = User.builder()
				      .username("scott")
				      .password(passwordEncoder.encode("tiger"))
				      .authority(customerRole)
				      .build();
			
			userRepository.saveAll(Arrays.asList(springUser, user, scottUser));
			
			log.info("Users loaded: {}", userRepository.count());
		}
		
	}

}
