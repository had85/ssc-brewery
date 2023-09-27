package guru.sfg.brewery.web.controllers.api;

import static guru.sfg.brewery.bootstrap.DefaultBreweryLoader.ADMIN_USERNAME;
import static guru.sfg.brewery.bootstrap.DefaultBreweryLoader.DUNEDIN_DISTRIBUTING;
import static guru.sfg.brewery.bootstrap.DefaultBreweryLoader.KEY_WEST_DISTRIBUTING;
import static guru.sfg.brewery.bootstrap.DefaultBreweryLoader.KEY_WEST_USERNAME;
import static guru.sfg.brewery.bootstrap.DefaultBreweryLoader.ST_PETE_DISTRIBUTING;
import static guru.sfg.brewery.bootstrap.DefaultBreweryLoader.ST_PETE_USERNAME;
import static guru.sfg.brewery.web.controllers.api.BeerOrderControllerV2.API_BASE_URL;

import java.util.UUID;

import javax.transaction.Transactional;

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

class BeerOrderControllerV2Test extends BaseIT {

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
	@DisplayName("List orders tests")
	class ListBeerOrdersTests extends BaseIT {
		
		@Test
		void listOrdersNotAuth() throws Exception {
			
			mockMvc.perform(MockMvcRequestBuilders
					.get(API_BASE_URL)
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
					.get(API_BASE_URL)
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk());
		}
		
		@Test
		@WithUserDetails(ST_PETE_USERNAME)
		void listOrdersCustomer() throws Exception {
			
			mockMvc.perform(MockMvcRequestBuilders
					.get(API_BASE_URL)
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk());
		}
		
	}
	
	@Transactional
	@Nested
	@DisplayName("Check order status tests")
	class CheckBeerOrderStatusTests extends BaseIT {
		
		@Test
		void checkOrderStatusNotAuth() throws Exception {
		
			mockMvc.perform(MockMvcRequestBuilders
					.get(API_BASE_URL + "{orderId}", beerOrderId)
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
					.get(API_BASE_URL + "{orderId}", beerOrderId)
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk());
		}
		
		@Test
		@WithUserDetails(ST_PETE_USERNAME)
		void checkOrderStatusCustomer() throws Exception {
		
			mockMvc.perform(MockMvcRequestBuilders
					.get(API_BASE_URL + "{orderId}", beerOrderId)
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk());
		}
		
		@Test
		@WithUserDetails(KEY_WEST_USERNAME)
		void checkOrderStatusCustomerNotMatchingWithUser() throws Exception {
		
			mockMvc.perform(MockMvcRequestBuilders
					.get(API_BASE_URL + "{orderId}", beerOrderId)
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isNotFound());
		}
		
	}
	
}
