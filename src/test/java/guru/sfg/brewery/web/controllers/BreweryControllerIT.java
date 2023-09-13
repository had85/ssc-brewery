package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class BreweryControllerIT extends BaseIT {
	
	@DisplayName("Get brewery tests")
	@Nested
	class BreweryControllerITWithProvidedUsers {

		@ParameterizedTest(name = "#{index} with [{arguments}]")
		@CsvSource({
			"spring, guru",
			"scott, tiger"
		})
		void getBreweriesBasicAuthWithValidUsers(String username, String password) throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get("/brewery/breweries")
					.with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password)))
					.andExpect(MockMvcResultMatchers.status().isOk());
		}
		
		@ParameterizedTest(name = "#{index} with [{arguments}]")
		@CsvSource({
			"user, password"
		})
		void getBreweriesBasicAuthWithInvalidUser(String username, String password) throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get("/brewery/breweries")
					.with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password)))
					.andExpect(MockMvcResultMatchers.status().isForbidden());
		}
	}
}
