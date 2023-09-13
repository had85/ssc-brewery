package guru.sfg.brewery.repositories.security;

import org.springframework.data.jpa.repository.JpaRepository;

import guru.sfg.brewery.domain.security.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
