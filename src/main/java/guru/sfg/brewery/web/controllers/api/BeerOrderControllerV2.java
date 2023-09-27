package guru.sfg.brewery.web.controllers.api;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.perms.BeerOrderCreatePermission;
import guru.sfg.brewery.repositories.security.perms.BeerOrderPickupPermission;
import guru.sfg.brewery.repositories.security.perms.BeerOrderReadPermissionV2;
import guru.sfg.brewery.services.BeerOrderService;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderPagedList;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/orders/")
@RequiredArgsConstructor
public class BeerOrderControllerV2 {
	
	public static final String API_BASE_URL = "/api/v1/orders/";
	
	private final BeerOrderService beerOrderService;
	
	@BeerOrderReadPermissionV2
	@GetMapping
	public BeerOrderPagedList listOrders(@AuthenticationPrincipal User user, //spring injectuje usera iz security konteksta
			@RequestParam(name = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
			                             @RequestParam(name = "pageSize", defaultValue = "25", required = false) Integer pageSize) {
		if(user.isAdmin()) { //razmisliti da li je bolje imati poseban admin panel nego ovo ovako
			return beerOrderService.listOrders(PageRequest.of(pageNumber, pageSize));
		}
		
		return beerOrderService.listOrders(user.getCustomer().getId(), PageRequest.of(pageNumber, pageSize));
	
	}
	
	@BeerOrderCreatePermission
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BeerOrderDto placeOrder(@AuthenticationPrincipal User user, @RequestBody BeerOrderDto beerOrderDto) {
	
		return beerOrderService.placeOrder(user.getCustomer().getId(), beerOrderDto);
	}
	
	@BeerOrderReadPermissionV2
	@GetMapping("{orderId}")
	@ResponseStatus(HttpStatus.OK)
	public BeerOrderDto getOrderStatus(@PathVariable ("orderId") UUID orderId) {

		return beerOrderService.getOrderById(orderId);
	}
	
	@BeerOrderPickupPermission
	@PutMapping("{orderId}/pickup")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void pickupOrder(@AuthenticationPrincipal User user, @PathVariable ("orderId") UUID orderId) {
		
		beerOrderService.pickupOrder(user.getCustomer().getId(), orderId);
	}
	
}
