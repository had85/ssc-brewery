package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest //ucitavaju se samo mvc komponente, sve ostalo moramo da mokujemo da ne bi pucali
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

}
