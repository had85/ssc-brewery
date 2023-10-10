package guru.sfg.brewery.configuration;


import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import guru.sfg.brewery.security.RestHeaderAuthFilter;
import guru.sfg.brewery.security.RestParamsAuthFilter;
import guru.sfg.brewery.security.SSCPasswordEncoderFactories;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) //enable-ujemo anotacije koje
                                                                          //koje stavljamo direktno nad metodama
                                                                          //securedEnabled - starije anotacije
                                                                          //preAuthorize - novije koje koriste SpEl
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	private final UserDetailsService userDetailsService;
	
	private final DataSource dataSource;
	
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
		.formLogin(loginConfigurer -> {//cim krenemo da konfigurisemo formLogin
			                           //gubimo spring form autoconfig i moramo sve da pokrijemo
			loginConfigurer.loginProcessingUrl("/login") //gde ce forma slati podatke
			               .loginPage("/") //login page ce biti na index pageu
			               //skroz je ok da login forma bude na /login url-u kao posebna stranica
			               //pa kad se korisnik uspesno uloguje da se ide na index ili ko zna gde
			               .permitAll()
			               .successForwardUrl("/") //u slucaju da login uspe ide se na index page
			               .defaultSuccessUrl("/")
			               .failureUrl("/?error");//ako je greska pri loginu saljemo parametar error
		})
		.logout(logoutConfigurer -> {//zelimo da se izlogujemo klikom na link zato menjamo nacin
			                         //sa uopbicajenog post-a na GET metodu
			                         //ovaj matcher znaci da prihvatamo zahtev kao validan
			logoutConfigurer.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
			.logoutSuccessUrl("/?logout") //idemo na index stranicu kad smo se uspesno izlogovali	
			.permitAll();
		})	
		.httpBasic() //koristiti http basic
		.and()
		.headers().frameOptions().sameOrigin()//ako se aplikacija hostuje kao frame u okviru nekog domena
		                                       //dozvoljavamo requestove (npr ovo je potrebno da bi enable-ovali
		                                       //h2 konzolu da prikazuje gui
		.and()
//		.rememberMe().key("privatni_kljuc_za_generisanje_hash-a_cookie-ja")
//		             .userDetailsService(userDetailsService);//simple hash based token config
		.rememberMe()
		.tokenRepository(persistentTokenRepository())
		.userDetailsService(userDetailsService); //persistent hash config
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		
		return SSCPasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	
	@Bean //potrebno za config persistent tokena
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl jdbcRepo = new JdbcTokenRepositoryImpl(); 
		jdbcRepo.setDataSource(dataSource);
		return jdbcRepo;
	}
	
}
