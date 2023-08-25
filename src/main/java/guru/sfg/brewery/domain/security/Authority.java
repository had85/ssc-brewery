package guru.sfg.brewery.domain.security;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import guru.sfg.brewery.domain.BaseEntityLong;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder (toBuilder = true)
public class Authority extends BaseEntityLong {
	
	private String role;
	
	@ManyToMany(mappedBy = "authorities") //kad hibernate parsira ovo kazemo mu gledaj polje authorities i tamo ce ti sve biti objasnjeno kako je relacija mapirana
	private Set<User> users;
}
