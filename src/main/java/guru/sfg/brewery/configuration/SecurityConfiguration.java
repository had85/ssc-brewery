package guru.sfg.brewery.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import guru.sfg.brewery.security.RestHeaderAuthFilter;
import guru.sfg.brewery.security.RestParamsAuthFilter;
import guru.sfg.brewery.security.SSCPasswordEncoderFactories;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) //enable-ujemo anotacije koje
                                                                          //koje stavljamo direktno nad metodama
                                                                          //securedEnabled - starije anotacije
                                                                          //preAuthorize - novije koje koriste SpEl
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	public SecurityEvaluationContextExtension contextExtension() {
		return new SecurityEvaluationContextExtension(); //kako bi omogucili spring security anotacije da rade
		                                                 //nad spring data jpa repository metodama
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
//		.csrf().disable() //iskljucuje springov synchronizer token
//		                  //kad se korisinik uloguje dobija synchronizer token koji salje kroz sve zahteve
//		                  //svoje sesije ako nema synchronizer tokena odbija se zahtev
		.csrf().ignoringAntMatchers("/h2-console/**", "/api/**") //disable-ovan csrf samo za h2 konzolu i api resurse
		                                                         //na spring mvc funkcionise
		.and()
		.addFilterBefore(new RestParamsAuthFilter(authenticationManager(), new AntPathRequestMatcher("/api/**")), UsernamePasswordAuthenticationFilter.class) //zelimo da nas custom filter radi pre navedenog UsernamePasswordAuthenticationFilter
		.addFilterBefore(new RestHeaderAuthFilter(authenticationManager(), new AntPathRequestMatcher("/api/**")), RestParamsAuthFilter.class) //zelimo da nas custom filter radi pre navedenog UsernamePasswordAuthenticationFilter
		.authorizeRequests( authorize -> {
		   authorize.antMatchers("/", //match-uje samo pocetnu index stranu
				                      //ostali matcher-i match-uju staticke fajlove i imamo /login resurs
				                 "/resources/**",
				                 "/login", //posto je rec o mvc aplikaciji u network tabu browsera 
				                           //pratimo sta se blokira na index strani i to unosimo ovde
				                           //kako bi index strana mogla da se ucitava a ostalo ide pod security
				   				 "/webjars/**",
				   				 "/h2-console/**")
		   .permitAll();//dve zvezdice znace da matcher pokriva /webjars/prvinivo/druginivo
		})
		.authorizeRequests() //autorizuje requestove
		.anyRequest().authenticated() //svaki request mora biti autentifikovan
		.and()
		.formLogin() //koristiti login formu springovu
		.and()
		.httpBasic() //koristiti http basic
		.and()
		.headers().frameOptions().sameOrigin();//ako se aplikacija hostuje kao frame u okviru nekog domena
		                                       //dozvoljavamo requestove (npr ovo je potrebno da bi enable-ovali
		                                       //h2 konzolu da prikazuje gui
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		
		return SSCPasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	
}
