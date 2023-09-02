package guru.sfg.brewery.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import guru.sfg.brewery.security.RestHeaderAuthFilter;
import guru.sfg.brewery.security.RestParamsAuthFilter;
import guru.sfg.brewery.security.SSCPasswordEncoderFactories;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
		.csrf().disable()
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
		   .permitAll() //dve zvezdice znace da matcher pokriva /webjars/prvinivo/druginivo
		                                        //sve sleseve u nedogled nakon inicijlanog /**
		                                        //ako stavimo samo jednu zvezdicu webjars/* to znaci sve u samo prvom
		                                        //nivou posle inicijalnog slesa
		                 //ant matcheri pomocu stringa url-a mozemo konfigurisati finije pravila za resurse
		                 //u ovom slucaju kad se ide na index stranu nece se raditi autorizacija/authentifikacija
		                 //sve sto dozvoljavamo mora da bude navedeno u konfigu
		                 //PRE svega sto branimo kako bi sve radilo
		   .antMatchers("/beers*",
				        "/beers/find")
		   .permitAll()
		   
		   .antMatchers(HttpMethod.GET, "/api/v1/beer/**")
		   .permitAll()
		   .regexMatchers(HttpMethod.GET, "/beers/[a-f0-9]{8}(?:-[a-f0-9]{4}){4}[a-f0-9]{8}")
		   //svi get /beer/uuid resursi ce biti matchovani
		   // mvcMatchers(HttpMethod.GET, "/beers/{beerId}")
		   //postoji  i mvcMatchers gde je samo drugacija sintaksa, slicnija request mapping sintaksi u 
		   //kontrolerima , medjutim to ne radi posao jer /beers/new i beers/123 oba prolaze a nama treba samo
		   //beers/123 i bilo koja kombinacija uuid-a da prolazi
		   .permitAll();
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
