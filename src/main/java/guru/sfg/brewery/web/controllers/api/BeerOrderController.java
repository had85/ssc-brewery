package guru.sfg.brewery.web.controllers.api;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import guru.sfg.brewery.repositories.security.perms.BeerOrderCreatePermission;
import guru.sfg.brewery.repositories.security.perms.BeerOrderPickupPermission;
import guru.sfg.brewery.repositories.security.perms.BeerOrderReadPermission;
import guru.sfg.brewery.services.BeerOrderService;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderPagedList;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/customers/{customerId}/")
@RequiredArgsConstructor
public class BeerOrderController {
	
	public static final String API_BASE_URL = "/api/v1/customers";
	
	private final BeerOrderService beerOrderService;
	
	@BeerOrderReadPermission
	@GetMapping("orders")
	public BeerOrderPagedList listOrders(@PathVariable("customerId") UUID customerId, 
			                             @RequestParam(name = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
			                             @RequestParam(name = "pageSize", defaultValue = "25", required = false) Integer pageSize) {
		
		return beerOrderService.listOrders(customerId, PageRequest.of(pageNumber, pageSize));
	
	}
	
	@BeerOrderCreatePermission
	@PostMapping("orders")
	@ResponseStatus(HttpStatus.CREATED)
	public BeerOrderDto placeOrder(@PathVariable("customerId") UUID customerId, @RequestBody BeerOrderDto beerOrderDto) {
		
		return beerOrderService.placeOrder(customerId, beerOrderDto);
	}
	
	@BeerOrderReadPermission
	@GetMapping("orders/{orderId}")
	@ResponseStatus(HttpStatus.OK)
	public BeerOrderDto getOrderStatus(@PathVariable("customerId") UUID customerId, @PathVariable ("orderId") UUID orderId) {
		
		return beerOrderService.getOrderById(customerId, orderId);
	}
	
	@BeerOrderPickupPermission
	@PutMapping("/orders/{orderId}/pickup")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void pickupOrder(@PathVariable("customerId") UUID customerId, @PathVariable ("orderId") UUID orderId) {
		
		beerOrderService.pickupOrder(customerId, orderId);
	}
	
}
