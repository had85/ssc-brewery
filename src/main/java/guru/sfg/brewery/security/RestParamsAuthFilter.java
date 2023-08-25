package guru.sfg.brewery.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import lombok.val;

//svaki custom filter mora da extenduje AbstractAuthenticationProcessingFilter
public class RestParamsAuthFilter extends AbstractAuthFilter {
	
	public RestParamsAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
		super(requiresAuthenticationRequestMatcher);
	}
	
	public RestParamsAuthFilter(AuthenticationManager authManager, RequestMatcher requestMatcher) {
		super(requestMatcher);
		this.setAuthenticationManager(authManager);
	}

	@Override
	protected UsernamePasswordAuthenticationToken getUsernamePasswordToken(HttpServletRequest request) {
		
		val username = request.getParameter("Api-Key");

		val password = request.getParameter("Api-Secret");

		return new UsernamePasswordAuthenticationToken(username, password);
	}
	
	@Override
	protected boolean hasCredentials(HttpServletRequest request) {
		return StringUtils.isEmpty(request.getParameter("Api-Key"))
				&& StringUtils.isEmpty(request.getParameter("Api-Secret"));
	}

}
