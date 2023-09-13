package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class CustomerControllerIT extends BaseIT {

	@DisplayName("Get cusustomers tests")
	@Nested
	class GetCustomersIT {

		@ParameterizedTest(name = "#{index} with [{arguments}]")
		@CsvSource({
			"spring, guru",
			"scott, tiger"
		})
		void getBreweriesBasicAuthWithValidUsers(String username, String password) throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get("/customers")
					.with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password)))
					.andExpect(MockMvcResultMatchers.status().isOk());
		}
		
		@ParameterizedTest(name = "#{index} with [{arguments}]")
		@CsvSource({
			"user, password"
		})
		void getBreweriesBasicAuthWithInvalidUser(String username, String password) throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get("/customers")
					.with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password)))
					.andExpect(MockMvcResultMatchers.status().isForbidden());
		}
		
		void getBreweriesBasicAuthWithAnon(String username, String password) throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get("/customers")
					.with(SecurityMockMvcRequestPostProcessors.anonymous()))
					.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		}
	}
	
	@DisplayName("Create customers tests")
	@Nested
	class CreateCustomersIT {

		@ParameterizedTest(name = "#{index} with [{arguments}]")
		@CsvSource({
			"spring, guru"
		})
		void getBreweriesBasicAuthWithValidUsers(String username, String password) throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.post("/customers/new")
					.param("customerName", "Foo Customer")
					.with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password)))
					.andExpect(MockMvcResultMatchers.status().is3xxRedirection()); //mvc koji vraca redirect
		}
		
		@ParameterizedTest(name = "#{index} with [{arguments}]")
		@CsvSource({
			"user, password"
		})
		void getBreweriesBasicAuthWithInvalidUser(String username, String password) throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.post("/customers/new")
					.param("customerName", "Foo Customer 2")
					.with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password)))
					.andExpect(MockMvcResultMatchers.status().isForbidden());
		}
		
		void getBreweriesBasicAuthWithAnon(String username, String password) throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.post("/customers/new")
					.param("customerName", "Foo Customer 3")
					.with(SecurityMockMvcRequestPostProcessors.anonymous()))
					.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		}
	}
}
