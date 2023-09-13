package guru.sfg.brewery.domain.security;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import guru.sfg.brewery.domain.BaseEntityLong;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Role extends BaseEntityLong {

	private String name;
    //cascade je propagacija kad cuvamo krovni dal ce se cuvati i deca
	//fetch je kad se povlaci iz baze sta se sve ucitava
	@ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
	@JoinTable(name = "role_authority", // ime tabele koje ima 2 kolone user_id koji referencira ovaj id od User
										// entiteta
			joinColumns = { @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID") }, inverseJoinColumns = {
					@JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID") }) // authority id koji referencira
																						// ID authority entiteta
	@Singular // dodaje se metoda add koja dodaje jedan element u authorities preko buildera
				// zgodno da bi izbegli da instanciramo kolekciju pa da dodajemo element pa tek
				// onda u builder argument
	private Set<Authority> authorities;
	
	@ManyToMany(mappedBy = "roles")
	private Set<User> users;
}
