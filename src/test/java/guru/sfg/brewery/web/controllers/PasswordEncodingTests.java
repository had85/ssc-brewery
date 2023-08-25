package guru.sfg.brewery.web.controllers;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.Md4PasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.util.DigestUtils;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@WebMvcTest
@Slf4j
public class PasswordEncodingTests extends BaseIT {
	
	static final String PASSWORD = "password";
	
	@Test
	//no operation pass encoder - ne radi nista cuva se u bazi gola lozinka
	void noOpPasswordEncoderTest() {
		val passwordEncoder = NoOpPasswordEncoder.getInstance();
		
		val noOpEncodedPassword = passwordEncoder.encode(PASSWORD);
		
		log.info("No op password encoder: {}", passwordEncoder.encode(PASSWORD));
		
		assertEquals(PASSWORD, noOpEncodedPassword);
		
		
	}
	
	@Test
	//md5 se ne preporucuje, suvise je prost, danasnji kompovi brzo provale sifru pogotovo ako ne koristimo
	//salt
	void hashMD5Test() {
		val digest =  DigestUtils.md5DigestAsHex(PASSWORD.getBytes());
		
		val digest2 =  DigestUtils.md5DigestAsHex(PASSWORD.getBytes());
		
		log.info("MD5 digest: {}", DigestUtils.md5DigestAsHex(PASSWORD.getBytes()));
		
		//5f4dcc3b5aa765d61d8327deb882cf99 uvek isti ako ne koristimo salt
		
		assertEquals(digest, digest2);
	}
	
	@Test
	//md4 se ne preporucuje, suvise je prost, danasnji kompovi brzo provale sifru pogotovo ako ne koristimo
	//koristi random salt unutar sebe
	void hashMD4Test() {
		val encoder = new Md4PasswordEncoder();
		val digest =  encoder.encode(PASSWORD);
		
		val digest2 =  encoder.encode(PASSWORD);
		
		
		log.info("MD4 digest (guru): {}", encoder.encode("guru"));
		
		log.info("MD4 digest (password): {}", encoder.encode(PASSWORD));
		
		log.info("MD4 digest (tiger): {}", encoder.encode("tiger"));

		
		//isti password uvek razlicito izgleda jer unutar sebe koristi random salt
		
		assertTrue(encoder.matches(PASSWORD, digest)); //kad vadimo hashovan password iz baze uporedjujemo ga sa 
		                                               //prosledjenim golim passwordom kroz metodu 
		                                               //encoder.matches(realPass,encodedPass)
		                                               //boolean je rezultat
		
		assertTrue(encoder.matches(PASSWORD, digest2));
	}
	
	@Test
	//koristi random salt unutar sebe
	void hashLDAPTest() {
		val encoder = new LdapShaPasswordEncoder();
		val digest =  encoder.encode(PASSWORD);
		
		val digest2 =  encoder.encode(PASSWORD);
		
		
		log.info("LDAP digest (guru): {}", encoder.encode("guru"));
		
		log.info("LDAP digest (password): {}", encoder.encode(PASSWORD));
		
		log.info("LDAP digest (tiger): {}", encoder.encode("tiger"));

		
		//isti password uvek razlicito izgleda jer unutar sebe koristi random salt
		
		assertTrue(encoder.matches(PASSWORD, digest)); //kad vadimo hashovan password iz baze uporedjujemo ga sa 
		                                               //prosledjenim golim passwordom kroz metodu 
		                                               //encoder.matches(realPass,encodedPass)
		                                               //boolean je rezultat
		
		assertTrue(encoder.matches(PASSWORD, digest2));
	}
	
	@Test
	//koristi random salt unutar sebe
	//glavna mana je sto je brz algoritam, napadac lako moza da salta razlicite sifre
	void hashSHA256Test() {
		val encoder = new StandardPasswordEncoder(); //nekada bio spring default 
		val digest =  encoder.encode(PASSWORD);
		
		val digest2 =  encoder.encode(PASSWORD);
		
		
		log.info("Sha256 digest (guru): {}", encoder.encode("guru"));
		
		log.info("Sha256 digest (password): {}", encoder.encode(PASSWORD));
		
		log.info("Sha256 digest (tiger): {}", encoder.encode("tiger"));

		
		//isti password uvek razlicito izgleda jer unutar sebe koristi random salt
		
		assertTrue(encoder.matches(PASSWORD, digest)); //kad vadimo hashovan password iz baze uporedjujemo ga sa 
		                                               //prosledjenim golim passwordom kroz metodu 
		                                               //encoder.matches(realPass,encodedPass)
		                                               //boolean je rezultat
		
		assertTrue(encoder.matches(PASSWORD, digest2));
	}
	
	@Test
	//koristi random salt unutar sebe
	//default spring algoritam u 2023 g.
	void hashBCryptTest() {
		val encoder = new BCryptPasswordEncoder(); //spring difolt
		
		val digest =  encoder.encode(PASSWORD);
		
		val digest2 =  encoder.encode(PASSWORD);
		
		
		log.info("BCrypt digest (guru): {}", encoder.encode("guru"));
		
		log.info("BCrypt digest (password): {}", encoder.encode(PASSWORD));
		
		log.info("BCrypt digest (tiger): {}", encoder.encode("tiger"));

		
		//isti password uvek razlicito izgleda jer unutar sebe koristi random salt
		
		assertTrue(encoder.matches(PASSWORD, digest)); //kad vadimo hashovan password iz baze uporedjujemo ga sa 
		                                               //prosledjenim golim passwordom kroz metodu 
		                                               //encoder.matches(realPass,encodedPass)
		                                               //boolean je rezultat
		
		assertTrue(encoder.matches(PASSWORD, digest2));
	}
	
	@Test
	//koristi random salt unutar sebe
	//default spring algoritam u 2023 g.
	void hashDelegatingPasswordEncodersTest() {
		
		val bcryptEncoder = new BCryptPasswordEncoder(); //spring difolt
		
		val sha256Encoder = new StandardPasswordEncoder();
		
		val bcrypt15Encoder = new BCryptPasswordEncoder(15); 
		
		
		log.info("BCrypt digest (guru): {}", bcryptEncoder.encode("guru"));
		
		log.info("Sha256 digest (password): {}", sha256Encoder.encode(PASSWORD));
		
		log.info("BCrypt15 digest (tiger): {}", bcrypt15Encoder.encode("tiger"));
	}
	
	
	
}
