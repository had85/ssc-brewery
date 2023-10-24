package guru.sfg.brewery.repositories.security;

import org.springframework.data.jpa.repository.JpaRepository;

import guru.sfg.brewery.domain.security.LoginSuccess;

public interface LoginSuccessRepository extends JpaRepository<LoginSuccess, Long>{
}
