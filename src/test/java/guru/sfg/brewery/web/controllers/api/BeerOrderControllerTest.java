package guru.sfg.brewery.web.controllers.api;

import static guru.sfg.brewery.bootstrap.DefaultBreweryLoader.*;
import static guru.sfg.brewery.web.controllers.api.BeerOrderController.API_BASE_URL;

import java.util.UUID;

import javax.transaction.Transactional;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.domain.BeerOrder;
import guru.sfg.brewery.domain.Customer;
import guru.sfg.brewery.repositories.BeerOrderRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import guru.sfg.brewery.web.controllers.BaseIT;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderLineDto;
import lombok.val;

class BeerOrderControllerTest extends BaseIT {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	BeerOrderRepository beerOrderRepository;

	@Autowired
	BeerRepository beerRepository;

	@Autowired
	ObjectMapper objMapper;

	Customer stPeteDistributingCustomer;

	Customer dunedinCustomer;

	Customer keyWestCustomer;

	Beer testBeer;
	
	String beerOrderId;

	@BeforeEach
	void setUp() {

		stPeteDistributingCustomer = customerRepository.findByCustomerName(ST_PETE_DISTRIBUTING).orElseThrow();

		dunedinCustomer = customerRepository.findByCustomerName(DUNEDIN_DISTRIBUTING).orElseThrow();

		keyWestCustomer = customerRepository.findByCustomerName(KEY_WEST_DISTRIBUTING).orElseThrow();

		testBeer = beerRepository.findAll()
				                 .stream()
				                 .findFirst()
				                 .orElseThrow();
		
		beerOrderId = stPeteDistributingCustomer.getBeerOrders()
				                                .stream()
				                                .findFirst()
				                                .map(BeerOrder::getId)
				                                .map(UUID::toString)
				                                .orElseThrow();
	}
	
	@Transactional
	@Nested
	@DisplayName("Create beer order tests")
	class CreateBeerOrderTests extends BaseIT {
		
		@Test
		void createOrderNotAuth() throws Exception {
			
			val beerOrderDto = buildOrderDto(stPeteDistributingCustomer, testBeer.getId());
			
			mockMvc.perform(MockMvcRequestBuilders
					.post(API_BASE_URL + "/{customerId}/orders", stPeteDistributingCustomer.getId().toString())
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objMapper.writeValueAsString(beerOrderDto))
					.with(SecurityMockMvcRequestPostProcessors.anonymous()))
					.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		}
		
		@Test
		@WithUserDetails(ADMIN_USERNAME)//spring userDetailsService izvlaci ovog usera u pozadini
	    //i puni security context sa detaljima i privilegijama
		void createOrderUserAdmin() throws Exception {
			
			val beerOrderDto = buildOrderDto(stPeteDistributingCustomer, testBeer.getId());
			
			mockMvc.perform(MockMvcRequestBuilders
					.post(API_BASE_URL + "/{customerId}/orders", stPeteDistributingCustomer.getId().toString())
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objMapper.writeValueAsString(beerOrderDto)))
					.andExpect(MockMvcResultMatchers.status().isCreated());
		}
		
		@Test
		@WithUserDetails(ST_PETE_USERNAME)
		void createOrderUserCustomer() throws Exception {
			
			val beerOrderDto = buildOrderDto(stPeteDistributingCustomer, testBeer.getId());
			
			mockMvc.perform(MockMvcRequestBuilders
					.post(API_BASE_URL + "/{customerId}/orders", stPeteDistributingCustomer.getId().toString())
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objMapper.writeValueAsString(beerOrderDto)))
					.andExpect(MockMvcResultMatchers.status().isCreated());
		}
		
		@Test
		@WithUserDetails(KEY_WEST_USERNAME)
		void createOrderUserWithNonMatchingCustomer() throws Exception {
			
			val beerOrderDto = buildOrderDto(stPeteDistributingCustomer, testBeer.getId());
			
			mockMvc.perform(MockMvcRequestBuilders
					.post(API_BASE_URL + "/{customerId}/orders", stPeteDistributingCustomer.getId().toString())
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objMapper.writeValueAsString(beerOrderDto)))
					.andExpect(MockMvcResultMatchers.status().isForbidden());
		}	
		
		private BeerOrderDto buildOrderDto(Customer customer, UUID beerId) {
			
			return BeerOrderDto.builder()
					 .customerId(customer.getId())
					 .customerRef(customer.getCustomerName() + "-Test")
					 .orderStatusCallbackUrl("https://example.com")
					 .beerOrderLines(Lists.newArrayList(BeerOrderLineDto.builder()
	                                                     .beerId(beerId)
	                                                     .orderQuantity(5)
	                                                     .build())
					 )
					.build();
			
		}
	}
	
	@Transactional
	@Nested
	@DisplayName("List orders tests")
	class ListBeerOrdersTests extends BaseIT {
		
		@Test
		void listOrdersNotAuth() throws Exception {
			
			mockMvc.perform(MockMvcRequestBuilders
					.get(API_BASE_URL + "/{customerId}/orders", stPeteDistributingCustomer.getId().toString())
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.contentType(MediaType.APPLICATION_JSON)
					.with(SecurityMockMvcRequestPostProcessors.anonymous()))
					.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		}
		
		@Test
		@WithUserDetails(ADMIN_USERNAME)
		void listOrdersAdmin() throws Exception {
			
			mockMvc.perform(MockMvcRequestBuilders
					.get(API_BASE_URL + "/{customerId}/orders", stPeteDistributingCustomer.getId().toString())
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk());
		}
		
		@Test
		@WithUserDetails(ST_PETE_USERNAME)
		void listOrdersCustomer() throws Exception {
			
			mockMvc.perform(MockMvcRequestBuilders
					.get(API_BASE_URL + "/{customerId}/orders", stPeteDistributingCustomer.getId().toString())
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk());
		}
		
		@Test
		@WithUserDetails(KEY_WEST_USERNAME)
		void listOrdersCustomerNotMatchingWithUser() throws Exception {
			
			mockMvc.perform(MockMvcRequestBuilders
					.get(API_BASE_URL + "/{customerId}/orders", stPeteDistributingCustomer.getId().toString())
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isForbidden());
		}	
	}
	
	
	@Transactional //posto se oslanjamo na vadjenje kolekcije iz entiteta, koji je lazy load-ovan
    //hibernate trazi transakcioni kontekst kako bi izvukao kolekciju.
    //@Transactional unutar testa se automatski rollback-uje
	@Nested
	@DisplayName("Get beer order status tests")
	class CheckBeerOrderStatusTests extends BaseIT {
		
		@Test
		void checkOrderStatusNotAuth() throws Exception {
		
			mockMvc.perform(MockMvcRequestBuilders
					.get(API_BASE_URL + "/{customerId}/orders/{orderId}",
							stPeteDistributingCustomer.getId().toString(),
							beerOrderId)
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.contentType(MediaType.APPLICATION_JSON)
					.with(SecurityMockMvcRequestPostProcessors.anonymous()))
					.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		}
		
		@Test
		@WithUserDetails(ADMIN_USERNAME)
		void checkOrderStatusAdmin() throws Exception {
		
			mockMvc.perform(MockMvcRequestBuilders
					.get(API_BASE_URL + "/{customerId}/orders/{orderId}",
							stPeteDistributingCustomer.getId().toString(),
							beerOrderId)
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk());
		}
		
		@Test
		@WithUserDetails(ST_PETE_USERNAME)
		void checkOrderStatusCustomer() throws Exception {
		
			mockMvc.perform(MockMvcRequestBuilders
					.get(API_BASE_URL + "/{customerId}/orders/{orderId}",
							stPeteDistributingCustomer.getId().toString(),
							beerOrderId)
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk());
		}
		
		@Test
		@WithUserDetails(KEY_WEST_USERNAME)
		void checkOrderStatusCustomerNotMatchingWithUser() throws Exception {
		
			mockMvc.perform(MockMvcRequestBuilders
					.get(API_BASE_URL + "/{customerId}/orders/{orderId}",
							stPeteDistributingCustomer.getId().toString(),
							beerOrderId)
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isForbidden());
		}
		
	}

	
	@Transactional //posto se oslanjamo na vadjenje kolekcije iz entiteta, koji je lazy load-ovan
    //hibernate trazi transakcioni kontekst kako bi izvukao kolekciju.
    //@Transactional unutar testa se automatski rollback-uje
	@Nested
	@DisplayName("Pickup beer order tests")
	class PickupBeerOrderTests extends BaseIT {

		@Test
		void pickupOrderAsAnon() throws Exception {
		
			mockMvc.perform(MockMvcRequestBuilders
				.put(API_BASE_URL + "/{customerId}/orders/{orderId}/pickup",
						stPeteDistributingCustomer.getId().toString(),
						beerOrderId)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.contentType(MediaType.APPLICATION_JSON)
				.with(SecurityMockMvcRequestPostProcessors.anonymous()))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		}
		
		@Test
		@WithUserDetails(ADMIN_USERNAME)
		void pickupOrderAsAdmin() throws Exception {
		
			mockMvc.perform(MockMvcRequestBuilders
				.put(API_BASE_URL + "/{customerId}/orders/{orderId}/pickup",
						stPeteDistributingCustomer.getId().toString(),
						beerOrderId)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
		}

		@Test
		@WithUserDetails(ST_PETE_USERNAME)
		void pickupOrderAsCustomer() throws Exception {
		
			mockMvc.perform(MockMvcRequestBuilders
				.put(API_BASE_URL + "/{customerId}/orders/{orderId}/pickup",
						stPeteDistributingCustomer.getId().toString(),
						beerOrderId)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
		}

		@Test
		@WithUserDetails(KEY_WEST_USERNAME)
		void pickupOrderAsDifferentCustomer() throws Exception {
		
			mockMvc.perform(MockMvcRequestBuilders
				.put(API_BASE_URL + "/{customerId}/orders/{orderId}/pickup",
						stPeteDistributingCustomer.getId().toString(),
						beerOrderId)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
		}
		
	}

}
