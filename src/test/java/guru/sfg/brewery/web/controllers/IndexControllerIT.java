package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest
public class IndexControllerIT extends BaseIT {
	
	@Test
	void testIndexPageGet() throws Exception {
		
		mockMvc.perform(MockMvcRequestBuilders.get("/"))
	       .andExpect(MockMvcResultMatchers.status().isOk());
	}

}
