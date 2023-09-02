package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

 //ucitavaju se samo mvc komponente, sve ostalo moramo da mokujemo da ne bi pucali
            //nullpointer-i
public class BeerRestControllerIT extends BaseIT {

	
	@Test
	void findBeers() throws Exception {	
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beer/")
		       .with(SecurityMockMvcRequestPostProcessors.anonymous()))
		       .andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@Test
	void findBeerById() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beer/")
				.param("beerId", "2332")
			    .with(SecurityMockMvcRequestPostProcessors.anonymous()))
		       .andExpect(MockMvcResultMatchers.status().isOk());
	} 
	
	@Test
	void deleteBeerByIdCustomSecurity() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/{beerId}", "e58ed763-928c-4155-bee9-fdbaaadc15f3")
				.header("Api-Key", "spring")
				.header("Api-Secret", "guru"))
		       .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
	} 
	
	
	@Test
	void deleteBeerByIdCustomSecurityBadCreds() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/{beerId}", "e58ed763-928c-4155-bee9-fdbaaadc15f3")
				.header("Api-Key", "spring")
				.header("Api-Secret", "guruXXX"))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
	} 
	
	@Test
	void deleteBeerByIdCustomSecurityUrlParams() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/{beerId}", "e58ed763-928c-4155-bee9-fdbaaadc15f3")
				.param("Api-Key", "spring")
				.param("Api-Secret", "guru"))
		       .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
	} 
	
	
	@Test
	void deleteBeerByIdCustomSecurityUrlParamsBadCreds() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/{beerId}", "e58ed763-928c-4155-bee9-fdbaaadc15f3")
				.param("Api-Key", "spring")
				.param("Api-Secret", "guruXXX"))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
	} 
	
	@Test
	void deleteBeerByIdBasicAuth() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/{beerId}", "e58ed763-928c-4155-bee9-fdbaaadc15f3")
				.with(SecurityMockMvcRequestPostProcessors.httpBasic("spring", "guru")))
		       .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
	} 
	
	@Test
	void deleteBeerByIdBasicAuthBadCreds() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/{beerId}", "e58ed763-928c-4155-bee9-fdbaaadc15f3")
				.with(SecurityMockMvcRequestPostProcessors.httpBasic("spring", "guruXXX")))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
	} 
	
	@Test
	void deleteBeerByIdBasicAuthBadCredsV2() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/{beerId}", "e58ed763-928c-4155-bee9-fdbaaadc15f3")
				.with(SecurityMockMvcRequestPostProcessors.httpBasic("", "")))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
	}
	
	@Test
	void deleteBeerByIdBasicAuthBadCredsV3() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/{beerId}", "e58ed763-928c-4155-bee9-fdbaaadc15f3")
				.with(SecurityMockMvcRequestPostProcessors.httpBasic(null, null)))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
	} 
	
	@Test
	void deleteBeerByIdUnauthorized() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/{beerId}", "e58ed763-928c-4155-bee9-fdbaaadc15f3"))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
	} 

}
