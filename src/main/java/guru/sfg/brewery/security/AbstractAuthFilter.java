package guru.sfg.brewery.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractAuthFilter extends AbstractAuthenticationProcessingFilter {

	public AbstractAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
		super(requiresAuthenticationRequestMatcher);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {

		val userToken = getUsernamePasswordToken(request);
		
		log.info("Authenticating user: {}", userToken.getPrincipal());

		//po difoltu je inmemory auth manager ali se moze overajdovati da se koristi npr baza itd.
		return getAuthenticationManager().authenticate(userToken);
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		//ako nemamo specijalne kredencijale preskacemo ovaj filter i idemo dole niz chain
		if(hasCredentials(request)){
			chain.doFilter(request, response);
			return;
		}
		
		//ako imamo kredencijale idemo u default doFilter
		//koji poziva attemptAuthentication i ako uspe ide na successfulAuthentication
		//obe metode smo overajdovali da rade ono sto nam treba
		super.doFilter(request, response, chain);
	}

	@Override
	//disable-ujemo redirect nakon uspesnog logina tako sto kopiramo sve iz super metode
	//a izostavljamo deo gde se radi redirect
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		
		if (log.isDebugEnabled()) {
			log.debug("Authentication success. Updating SecurityContextHolder to contain: "
					+ authResult);
		}
		//krucijalna linija koja ubacuje authresult u memoriju
		SecurityContextHolder.getContext().setAuthentication(authResult);
	}
	
	protected abstract boolean hasCredentials(HttpServletRequest request);

	protected abstract UsernamePasswordAuthenticationToken getUsernamePasswordToken(HttpServletRequest request);
	
}
