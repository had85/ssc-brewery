package guru.sfg.brewery.security.google;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import guru.sfg.brewery.domain.security.User;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Google2FAAuthFilter extends GenericFilterBean {
	
	private final AuthenticationTrustResolver authenticationTrustResolver;
	
	private final Google2FAFailureHandler google2faFailureHandler;
	
	private final List<RequestMatcher> requestMatchers;
	
	public Google2FAAuthFilter(AuthenticationTrustResolver authenticationTrustResolver,
			Google2FAFailureHandler google2faFailureHandler) {
		this.authenticationTrustResolver = authenticationTrustResolver;
		this.google2faFailureHandler = google2faFailureHandler;
		
	    this.requestMatchers = Stream.of(new RequestMatcher[] {
	    		new AntPathRequestMatcher("/user/verify2fa"),
	    		new AntPathRequestMatcher("/resources/**"),
	    		PathRequest.toStaticResources().atCommonLocations()
	    }).collect(Collectors.toList());
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		val servletRequest = (HttpServletRequest) request;
		
		val servletResponse = (HttpServletResponse) response;
		
		if(requestMatchers
				.stream()
				.anyMatch(matcher -> matcher.matches(servletRequest))) { //zelimo da preskocimo ovaj filter
			                                                                       //ako se zahtevaju staticki resursi
			                                                                       //ili ako korisnik ide na verifikacionu
			                                                                       //formu
			chain.doFilter(servletRequest, servletResponse);
			return;
		}
		
		val authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if(authentication != null && !authenticationTrustResolver.isAnonymous(authentication)) { 
			//znaci da je korisnik ulogovan i da nije anoniman
			log.info("Processing with google auth filter");
			if(authentication.getPrincipal() != null && authentication.getPrincipal() instanceof User) {
				val user = (User) authentication.getPrincipal();
				if(user.isUsing2FA() && user.isTotpRequired()) { //ako je korisnik podesio google auth ulogovao se
					                                             //ali nije uneo sifru totpRequired je true
					                                             //onda ulazi ovde gde ih redirektujemo na formu za unos
					                                             //totp koda
					google2faFailureHandler.onAuthenticationFailure(servletRequest, servletResponse, null);
					return; //redirektujemo ga ne ide dalje kroz filter
				}
			}
			
		}
		//prosledjujemo dalje
		chain.doFilter(servletRequest, servletResponse);
	}

}
