package guru.sfg.brewery.services;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserUnlockService {
	
	private final UserRepository userRepository;
	
	@Scheduled(fixedDelay = 5000)
	public void unlockUsers() {
		log.info("Running Unlock Accounts");
		
		val unlockedUsers = userRepository.findAllByAccountNonLockedAndLastModifiedDateIsBefore(false,
				             Timestamp.valueOf(LocalDateTime.now().minusSeconds(30)))
				             .stream()
				             .map(lockedUser -> {
					               lockedUser.setAccountNonLocked(true);
				                   return lockedUser;
				              })
				             .collect(Collectors.toList());
		if(unlockedUsers.size() > 0) {
		    log.info("Unlocking user accounts. No. of unlocked accounts: {}", unlockedUsers.size());
			userRepository.saveAll(unlockedUsers);
		}
		
		
		
	}

}
