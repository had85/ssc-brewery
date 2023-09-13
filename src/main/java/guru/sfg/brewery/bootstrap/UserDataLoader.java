package guru.sfg.brewery.bootstrap;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.Role;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.RoleRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDataLoader implements CommandLineRunner {

	private final RoleRepository roleRepository;
	
	private final AuthorityRepository authorityRepository;

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public void run(String... args) throws Exception {

		if (userRepository.count() == 0) {

			val beerCreate = Authority.builder().permission("beer.create").build();
			val beerRead = Authority.builder().permission("beer.read").build();
			val beerUpdate = Authority.builder().permission("beer.update").build();
			val beerDelete = Authority.builder().permission("beer.delete").build();

			val customerCreate = Authority.builder().permission("customer.create").build();
			val customerRead = Authority.builder().permission("customer.read").build();
			val customerUpdate = Authority.builder().permission("customer.update").build();
			val customerDelete = Authority.builder().permission("customer.delete").build();

			val breweryCreate = Authority.builder().permission("brewery.create").build();
			val breweryRead = Authority.builder().permission("brewery.read").build();
			val breweryUpdate = Authority.builder().permission("brewery.update").build();
			val breweryDelete = Authority.builder().permission("brewery.delete").build();
			
			authorityRepository.saveAll(List.of(beerCreate, beerRead, beerUpdate, beerDelete,
                    breweryCreate, breweryRead, breweryUpdate, breweryDelete,
                    customerCreate, customerRead, customerUpdate, customerDelete));

			val adminRole = Role.builder().name("ADMIN")
					.authorities(new HashSet<>(Set.of(beerCreate, beerRead, beerUpdate, beerDelete,
							                          breweryCreate, breweryRead, breweryUpdate, breweryDelete,
							                          customerCreate, customerRead, customerUpdate, customerDelete)))
					                           .build();
			         //ne mozemo cist Set.of jer se hibernate buni kad cuva immutable kolekcije koje setujemo u 
			         //entitete.

			val userRole = Role.builder().name("USER").build();

			val customerRole = Role.builder().name("CUSTOMER").authorities(new HashSet<>(Set.of(beerRead, customerRead, breweryRead))).build();

			roleRepository.saveAll(Arrays.asList(adminRole, userRole, customerRole));

			val springUser = User.builder().username("spring").password(passwordEncoder.encode("guru")).role(adminRole)
					.build();

			val user = User.builder().username("user").password(passwordEncoder.encode("password")).role(userRole)
					.build();

			val scottUser = User.builder().username("scott").password(passwordEncoder.encode("tiger"))
					.role(customerRole).build();

			userRepository.saveAll(Arrays.asList(springUser, user, scottUser));

			log.info("Users loaded: {}", userRepository.count());
		}

	}

}
