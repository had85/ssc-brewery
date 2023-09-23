package guru.sfg.brewery.repositories.security.perms;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('order.create') "+ 
		  "OR (hasAuthority('customer.order.create') " +
		  "AND @beerOrderAuthManager.customerIdMatches(authentication, #customerId))")
public @interface BeerOrderCreatePermission {}
