package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import guru.sfg.brewery.repositories.BeerInventoryRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import guru.sfg.brewery.services.BeerService;
import guru.sfg.brewery.services.BreweryService;

@SpringBootTest
public class BaseIT {

	protected MockMvc mockMvc;
	
	@Autowired
	WebApplicationContext webApplicationContext;
	
	@MockBean //dodaje mock kroz spring di, metode moramo posebno da konfigurisemo sta ce da se vraca
	BeerRepository beerRepository;
	
	@MockBean
	BeerInventoryRepository beerInventoryRepository;
	
	@MockBean
	BreweryService breweryService;
	
	@MockBean
	CustomerRepository customerRepository;
	
	@MockBean
	BeerService beerService;
	
	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				    .apply(SecurityMockMvcConfigurers.springSecurity()) //enable-ujemo spring security
					.build();
	}
}
