package guru.sfg.brewery.web.controllers;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import com.warrenstrange.googleauth.KeyRepresentation;

import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/user")
@Controller
@RequiredArgsConstructor
public class UserController {
	
	private final UserRepository userRepository;
	
	private final GoogleAuthenticator googleAuthenticator;
	
	//ovde prikazujemo QR kod korisniku koji treba da se registruje na 2FA, mora da bude ulogovan
	@GetMapping("/register2fa") //kad se dodje na ovaj resurs vracamo html stranicu iz templates/user/register2fa.html
	public String register2FaViewPage(Model model) {
		
		val user = getUser();
		val url = GoogleAuthenticatorQRGenerator.getOtpAuthURL("SFG", user.getUsername(),
				googleAuthenticator.createCredentials(user.getUsername()));
		//create credentials pravi kredencijale i cuva ih kroz nas IRepository koji smo nasledili za tog usera
		//svako otvaranje stranice generise novi qr kod i novi secret u bazi vezan za korisnika
		//google authenticator cima spoljni api kako bi generisao QR kod koji prikazujemo
		model.addAttribute("googleUrl", url); //ovde vracamo url koji kroz img tagove renderujemo kao QR kod
		return "user/register2fa";
	}
	
	@PostMapping ("/register2fa")//kad korisnik sabmituje formu, salje se parametar i vracamo korisnika na index stranicu ako je uspesna
	                             //otp kod verifikacija
	public String confirm2Fa(@RequestParam Integer verificationCode) {
		
		log.info("Entered code is: {}", verificationCode);
		
		val user = getUser();
		//spusta se u bazu dohvata usera pomocu nasih repository-ja i verifikuje dal je prosledjen kod ok,
		//u skladu sa prosledjenom tajnom i verifikacionim kodom
		//ako je kod dobar vodimo korisnika na index stranicu
		if(googleAuthenticator.authorizeUser(user.getUsername(), verificationCode)) {;
		   val currentUser = userRepository.findById(user.getId()).orElseThrow(); //zbog toga sto je korisnik posle logina
		                                                                          //mozda menjao svoje podatke spustamo se
		                                                                          //u bazu
		   currentUser.setUsing2FA(true); //posto je verifikovan cekiramo da sada uspesno koristi 2fa u bazi
		   userRepository.save(currentUser);
		   return "index";
		}
		//lose unesen kod idemo nazad na registraciju opciono dodati poruku o gresci
		return "user/register2fa";
	}
	
	@GetMapping("/verify2fa")
	public String verify2faViewPage() {
		
		return "user/verify2fa";
	}
	
	@PostMapping("/verify2fa")
	public String verify2fa(@RequestParam Integer verificationCode) {
		
		val user = getUser();
		
		if(googleAuthenticator.authorizeUser(user.getUsername(), verificationCode)) {;
            user.setTotpRequired(false);//oznacavamo flag da filteri ne presrecu vise ovog ulogovanog korisnika
                                        //jer je vec uspesno uneo kod po loginu, za sledeci login ce opet biti true
                                        //pa ce morati opet da unosi
		   return "index";
		}
		
		return "user/verify2fa";
	}
	
	private User getUser() {
		//hvatamo ulogovane usere, koji ako su se ulogovali principal je user objekat
		//ovo se moze alternativno preko reuqest scope dependency injectiona odraditi
		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
	
}
