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

//izbegavamo @Data anotaciju jer to string sa many to many relacijama moze da udje u infi loop. ToString i equalsAndHashocode anotacije moramo pazljivo da dodeljujemo
//kad imamo bidirekcionalne veze medju entitetima

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder (toBuilder = true)
public class User extends BaseEntityLong {
	
	private String username;
	private String password;
	
	//cascadeType.MERGE - svako azuriranje ovog entiteta se automatski propagira na veznu tabelu
	//fetchType da li cekamo da se metoda pozove pa da se radi upit ili po prvom cupanju da se ucita i ova kolekcija
	@ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JoinTable(name = "user_authority", //ime tabele koje ima 2 kolone user_id koji referencira ovaj id od User entiteta
	  joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
	  inverseJoinColumns = {@JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID")}) //authority id koji referencira ID authority entiteta
	@Singular //dodaje se metoda add koja dodaje jedan element u authorities preko buildera
	          //zgodno da bi izbegli da instanciramo kolekciju pa da dodajemo element pa tek onda u builder argument
	private Set<Authority> authorities;
	
	//kad pravimo objekat preko builder-a automatski ce ova polja biti setovana
	@Builder.Default
	private boolean accountNonExpired = true;
	
	@Builder.Default
	private boolean accountNonLocked = true;
	
	@Builder.Default
	private boolean credentialsNonExpired = true;
	
	@Builder.Default
	private boolean enabled = true;
}
