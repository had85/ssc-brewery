package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


public class BeerControllerIT extends BaseIT {
	
	@Test
	//koristimo kad testiramo biznis logiku, i premoscujemo security
	@WithMockUser(username = "gedza", authorities = "beer.read") //ako anotiramo sa ovom anotacijom, spring security se fakticki
	                       //overajduje, jer proglasavamo validnog korisnika unapred
	                       //ako imamo granuliran security dodajemo ovde authoritije/role u okviru same anotacije
	                       //mokujemo login, nema veze koji je user pravi u bazi
	void findBeers() throws Exception {
		
		mockMvc.perform(MockMvcRequestBuilders.get("/beers/find"))
		       .andExpect(MockMvcResultMatchers.status().isOk())
		       .andExpect(MockMvcResultMatchers.view().name("beers/findBeers"))
		       .andExpect(MockMvcResultMatchers.model().attributeExists("beer"));
	}
	
//	@Test
//	//koristimo kad ocemo da testiramo sam security kako radi
	//zakomentarisan jer cemo namenski da iskljucimo security nad GET beer resursima
//	void findBeersWithHttpBasicAuth() throws Exception {
//		
//		mockMvc.perform(MockMvcRequestBuilders.get("/beers/find")
//				         .with(SecurityMockMvcRequestPostProcessors.httpBasic("spring", "guru"))) //enable slanja http hedera uz request
//		                                                                                          //gde se testira pravi http basic koji smo podesili u projektu
//		                                                                                          //username:pass se enkoduju u base64 i salju se kroz Authorization header
//		       .andExpect(MockMvcResultMatchers.status().isOk())
//		       .andExpect(MockMvcResultMatchers.view().name("beers/findBeers"))
//		       .andExpect(MockMvcResultMatchers.model().attributeExists("beer"));
//	}
	
	
	@Test
	//koristimo kad ocemo da testiramo sam security kako radi
	void findBeersNoAuth() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/beers/find")
				        .with(SecurityMockMvcRequestPostProcessors.anonymous()))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
	}
	
	@ParameterizedTest
	@MethodSource("getAdminAndCustomer")
	//koristimo kad ocemo da testiramo sam security kako radi
	void findBeersAllRoles(String username, String password) throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/beers/find")
				        .with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password)))
		       .andExpect(MockMvcResultMatchers.status().isOk())
		       .andExpect(MockMvcResultMatchers.view().name("beers/findBeers"))
		       .andExpect(MockMvcResultMatchers.model().attributeExists("beer"));
	}

}
