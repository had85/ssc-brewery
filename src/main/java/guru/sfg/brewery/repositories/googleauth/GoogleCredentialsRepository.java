package guru.sfg.brewery.repositories.googleauth;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import com.warrenstrange.googleauth.ICredentialRepository;

import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ovo ce koristiti 3rd party biblioteka za pristup nasim korisnicima kako bi
 * cuvala i dohvatala secret key potreban za njeno funkcionisanje
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleCredentialsRepository implements ICredentialRepository {

	private final UserRepository userRepository;

	@Override
	public String getSecretKey(String username) {
		return userRepository.findByUsername(username)
				             .map(User::getSecret2FA)
				             .orElseThrow();
	}

	@Override
	@Transactional
	public void saveUserCredentials(String username, String secretKey, int validationCode, List<Integer> scratchCodes) {
		userRepository.findByUsername(username)
					  .map(user -> {
							log.info("Saving key: {}, validationCode: {}", secretKey, validationCode);

							user.setSecret2FA(secretKey);
							user.setUsing2FA(true);
							return user;
					  })
					  .ifPresent(userRepository::save);
	}

}
