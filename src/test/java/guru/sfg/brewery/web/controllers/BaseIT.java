package guru.sfg.brewery.web.controllers;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
public class BaseIT {

	protected MockMvc mockMvc;
	
	@Autowired
	WebApplicationContext webApplicationContext;
	
	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				    .apply(SecurityMockMvcConfigurers.springSecurity()) //enable-ujemo spring security
					.build();
	}
	
	public static Stream<Arguments> getAllUsers(){
		return Stream.of(
				Arguments.of("spring", "guru"),
				Arguments.of("scott", "tiger"),
				Arguments.of("user", "password")
		);
	}
	
	public static Stream<Arguments> getAdminAndCustomer(){
		return Stream.of(
				Arguments.of("spring", "guru"),
				Arguments.of("scott", "tiger")
		);
	}
}
