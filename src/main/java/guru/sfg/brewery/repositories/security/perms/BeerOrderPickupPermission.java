package guru.sfg.brewery.repositories.security.perms;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('order.read') "+ 
		  "OR (hasAuthority('customer.order.read') " +
		  "AND @beerOrderAuthManager.customerIdMatches(authentication, #customerId))")
public @interface BeerOrderPickupPermission {}
