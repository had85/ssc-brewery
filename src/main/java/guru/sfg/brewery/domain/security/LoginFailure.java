package guru.sfg.brewery.domain.security;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import guru.sfg.brewery.domain.BaseEntityLong;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder (toBuilder = true)
@ToString
public class LoginFailure extends BaseEntityLong {
	
	private String username;//ako je username pogresan
	
	@ManyToOne
	private User user; //ako je username dobar a password pogresan
	
	private String sourceIp;
}
