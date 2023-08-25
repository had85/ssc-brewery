package guru.sfg.brewery.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import lombok.val;

//svaki custom filter mora da extenduje AbstractAuthenticationProcessingFilter
public class RestHeaderAuthFilter extends AbstractAuthFilter {
	
	public RestHeaderAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
		super(requiresAuthenticationRequestMatcher);
	}
	
	public RestHeaderAuthFilter(AuthenticationManager authManager, RequestMatcher requestMatcher) {
		super(requestMatcher);
		this.setAuthenticationManager(authManager);
	}
	
	@Override
	protected UsernamePasswordAuthenticationToken getUsernamePasswordToken(HttpServletRequest request) {
		
		val username = request.getHeader("Api-Key");

		val password = request.getHeader("Api-Secret");

		return new UsernamePasswordAuthenticationToken(username, password);
	}

	@Override
	protected boolean hasCredentials(HttpServletRequest request) {
		return StringUtils.isEmpty(request.getHeader("Api-Key"))
				&& StringUtils.isEmpty(request.getHeader("Api-Secret"));
	}

}
