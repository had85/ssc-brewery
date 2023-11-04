package guru.sfg.brewery.domain.security;

import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import guru.sfg.brewery.domain.BaseEntityLong;
import guru.sfg.brewery.domain.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

//izbegavamo @Data anotaciju jer to string sa many to many relacijama moze da udje u infi loop. ToString i equalsAndHashocode anotacije moramo pazljivo da dodeljujemo
//kad imamo bidirekcionalne veze medju entitetima

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder (toBuilder = true)
public class User extends BaseEntityLong implements UserDetails, CredentialsContainer{

	private static final long serialVersionUID = 1L;
	
	private String username;
	private String password;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private Customer customer;
	
	//cascadeType.MERGE - svako azuriranje ovog entiteta se automatski propagira na veznu tabelu
	//fetchType da li cekamo da se metoda pozove pa da se radi upit ili po prvom cupanju da se ucita i ova kolekcija
	//eager - izbegavamo n+1 problem, jer se radi join upit
	@ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
	@JoinTable(name = "user_role", //ime tabele koje ima 2 kolone user_id koji referencira ovaj id od User entiteta
	  joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
	  inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")}) //authority id koji referencira ID authority entiteta
	@Singular //dodaje se metoda add koja dodaje jedan element u authorities preko buildera
	          //zgodno da bi izbegli da instanciramo kolekciju pa da dodajemo element pa tek onda u builder argument
	private Set<Role> roles;
	
	//ovo ce pozivati spring interno pa pakujemo tako da svaki authority koji je izvucen iz baze
	//prosledimo springu koji ce preko anotacija da uporedjuje i ukrsta koji user sme sta da uradi
	public Set<GrantedAuthority> getAuthorities(){
		return roles.stream()
				.map(Role::getAuthorities)
				.flatMap(Set::stream)
				.map(authority -> new SimpleGrantedAuthority(authority.getPermission()))
				.collect(Collectors.toSet());
	}
	
	//kad pravimo objekat preko builder-a automatski ce ova polja biti setovana
	@Builder.Default
	private boolean accountNonExpired = true;
	
	@Builder.Default
	private boolean accountNonLocked = true;
	
	@Builder.Default
	private boolean credentialsNonExpired = true;
	
	@Builder.Default
	private boolean enabled = true;
	
	@Builder.Default
	private boolean using2FA = false; //spring security na osnovu ovoga odlucuje dal rutira korisnika 
	                                  //na 2FA Verifikaciju ili ne
	                                  //podesava se na registraciji tj unosi se
	
	private String secret2FA;//unosi se na registraciji
	
	@Builder.Default
	@Transient
	private boolean totpRequired = true; //da li je korisnik uneo totp, ako jeste ovo se svicuje u false
	                                    //spring security filter ce ovo cackati nece se perzistirati

	//iskopirali iz spring implementacije
	@Override
	public void eraseCredentials() {
		password = null;		
	}
	
	public boolean isAdmin() {
		return roles.stream()
				.map(Role::getName)
				.filter(roleName -> "ADMIN".equalsIgnoreCase(roleName))
				.count() > 0;
				
	}
}
