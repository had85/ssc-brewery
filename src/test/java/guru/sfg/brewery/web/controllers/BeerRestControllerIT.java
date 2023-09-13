package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.web.model.BeerStyleEnum;

 //ucitavaju se samo mvc komponente, sve ostalo moramo da mokujemo da ne bi pucali
            //nullpointer-i
public class BeerRestControllerIT extends BaseIT {
	
	@Autowired
	BeerRepository beerRepository;

	
	@Test
	void findBeers() throws Exception {	
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beer/")
		       .with(SecurityMockMvcRequestPostProcessors.anonymous()))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
	}
	
	@Test
	void findBeerById() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beer/")
				.param("beerId", "2332")
			    .with(SecurityMockMvcRequestPostProcessors.anonymous()))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
	} 
	
	@Nested
	@DisplayName("Delete beer tests")
	class DeleteBeerTests {
		
		private Beer beer;
		
		@BeforeEach
		void setUp() {
			beer = Beer.builder()
					   .beerName("Delete Me")
					   .beerStyle(BeerStyleEnum.ALE)
					   .minOnHand(12)
					   .quantityToBrew(200)
					   .upc(System.currentTimeMillis() + "")
					   .build();
			
			beerRepository.saveAndFlush(beer); //bitno jer testovi postaju izolovani i ne ometaju druge testove
		}
		
		@Test
		void deleteBeerByIdCustomSecurity() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/{beerId}", beer.getId().toString())
					.header("Api-Key", "spring")
					.header("Api-Secret", "guru"))
			       .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		} 
		
		
		@Test
		void deleteBeerByIdCustomSecurityBadCreds() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/{beerId}", beer.getId().toString())
					.header("Api-Key", "spring")
					.header("Api-Secret", "guruXXX"))
			       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		} 
		
		@Test
		void deleteBeerByIdCustomSecurityUrlParams() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/{beerId}", beer.getId().toString())
					.param("Api-Key", "spring")
					.param("Api-Secret", "guru"))
			       .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		} 
		
		
		@Test
		void deleteBeerByIdCustomSecurityUrlParamsBadCreds() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/{beerId}", beer.getId().toString())
					.param("Api-Key", "spring")
					.param("Api-Secret", "guruXXX"))
			       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		} 
		
		@ParameterizedTest(name = "#{index} with [{arguments}]")
		@CsvSource({
			"spring, guru"
		})
		void deleteBeerByIdBasicAuthValidUsers(String username, String password) throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/{beerId}", beer.getId().toString())
					.with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password)))
			       .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		}
		
		@ParameterizedTest(name = "#{index} with [{arguments}]")
		@CsvSource({
			"user, password",
			"scott, tiger"
		})
		void deleteBeerByIdBasicAuthInvalidUser(String username, String password)  throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/{beerId}", beer.getId().toString())
					.with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password)))
			       .andExpect(MockMvcResultMatchers.status().isForbidden());
		}
		
		@Test
		void deleteBeerByIdBasicAuthBadCreds() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/{beerId}", beer.getId().toString())
					.with(SecurityMockMvcRequestPostProcessors.httpBasic("spring", "guruXXX")))
			       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		} 
		
		@Test
		void deleteBeerByIdBasicAuthBadCredsV2() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/{beerId}", beer.getId().toString())
					.with(SecurityMockMvcRequestPostProcessors.httpBasic("", "")))
			       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		}
		
		@Test
		void deleteBeerByIdBasicAuthBadCredsV3() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/{beerId}", beer.getId().toString())
					.with(SecurityMockMvcRequestPostProcessors.httpBasic(null, null)))
			       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		} 
		
		@Test
		void deleteBeerByIdUnauthorized() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/{beerId}", beer.getId().toString()))
			       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		} 
	}
	


}
