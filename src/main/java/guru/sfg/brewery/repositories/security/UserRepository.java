package guru.sfg.brewery.repositories.security;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import guru.sfg.brewery.domain.security.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByUsername(String username);
	
	List<User> findAllByAccountNonLockedAndLastModifiedDateIsBefore(Boolean accountNonLocked, Timestamp lastModifiedDate);
}
