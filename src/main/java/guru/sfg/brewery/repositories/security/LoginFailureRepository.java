package guru.sfg.brewery.repositories.security;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import guru.sfg.brewery.domain.security.LoginFailure;
import guru.sfg.brewery.domain.security.User;

public interface LoginFailureRepository extends JpaRepository<LoginFailure, Long> {
	
	List<LoginFailure> findAllByUserAndCreatedDateIsAfter(User user, Timestamp createdDate);
}
