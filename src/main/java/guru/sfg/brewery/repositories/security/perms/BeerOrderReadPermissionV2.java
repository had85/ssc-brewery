package guru.sfg.brewery.repositories.security.perms;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('order.pickup') OR hasAuthority('customer.order.pickup')")
public @interface BeerOrderReadPermissionV2 {}
