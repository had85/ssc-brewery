package guru.sfg.brewery.security;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import guru.sfg.brewery.domain.security.User;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * mozemo preko anotacija i spel-a da referenciramo metode bean-ova koji proveravaju security uslove
 * za nas, gde cross referenciramo user-a koji je u security context-u requesta i sa parametrima koje prosledje
 * koje mozemo da iscupamo kroz referencu
 * 
 * bitno je da imamo boolean metode unutar kojih mozemo da radimo sta nam je volja (spustanje u bazu,
 * eksterni rest pozivii itd)
 * 
 * @author batman
 *
 */

@Slf4j
@Component
public class BeerOrderAuthManager {
	
	/**
	 * 
	 * @param auth - {@link Authentication} - security contekst objekat
	 * @param customerId - parametar koji pomocu anotacije cupamo i prosledjujemo ovde
	 * @return da li se id customer-a slaze sa id-jem koji proskedjuje kao parametar
	 */
	public boolean customerIdMatches(Authentication auth, UUID customerId) {
		val authenticatedUser = (User) auth.getPrincipal(); //principal je nas domenski userDetails objekat
		log.info("User to be validated: {}", authenticatedUser);
		log.info("CustomerId:{}", customerId);
		return authenticatedUser.getCustomer().getId().equals(customerId);
	}

}
