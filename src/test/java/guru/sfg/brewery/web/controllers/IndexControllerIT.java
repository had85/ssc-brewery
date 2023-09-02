package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class IndexControllerIT extends BaseIT {
	
	@Test
	void testIndexPageGet() throws Exception {
		
		mockMvc.perform(MockMvcRequestBuilders.get("/"))
	       .andExpect(MockMvcResultMatchers.status().isOk());
	}

}
