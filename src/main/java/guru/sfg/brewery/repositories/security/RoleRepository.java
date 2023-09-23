package guru.sfg.brewery.repositories.security;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import guru.sfg.brewery.domain.security.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	
	Optional<Role> findByName(String customerName); //best practice je da se vraca optional iz repository-ja
}
