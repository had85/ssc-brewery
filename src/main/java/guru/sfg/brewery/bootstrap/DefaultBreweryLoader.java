/*
 *  Copyright 2020 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package guru.sfg.brewery.bootstrap;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.domain.BeerInventory;
import guru.sfg.brewery.domain.BeerOrder;
import guru.sfg.brewery.domain.BeerOrderLine;
import guru.sfg.brewery.domain.Brewery;
import guru.sfg.brewery.domain.Customer;
import guru.sfg.brewery.domain.OrderStatusEnum;
import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.Role;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.BeerInventoryRepository;
import guru.sfg.brewery.repositories.BeerOrderRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.BreweryRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.RoleRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;


/**
 * Created by jt on 2019-01-26.
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class DefaultBreweryLoader implements CommandLineRunner {
	
	public static final String ADMIN_USERNAME = "spring";
    public static final String ST_PETE_USERNAME = "stpete";
    public static final String DUNEDIN_USERNAME = "dunedin";
    public static final String KEY_WEST_USERNAME = "keywest";

    public static final String TASTING_ROOM = "Tasting Room";
    public static final String ST_PETE_DISTRIBUTING = "St. Pete Distributing";
    public static final String DUNEDIN_DISTRIBUTING = "Dunedin Distributing";
    public static final String KEY_WEST_DISTRIBUTING = "Key West Distributing";
    
    public static final String BEER_1_UPC = "0631234200036";
    public static final String BEER_2_UPC = "0631234300019";
    public static final String BEER_3_UPC = "0083783375213";

    private final BreweryRepository breweryRepository;
    private final BeerRepository beerRepository;
    private final BeerInventoryRepository beerInventoryRepository;
    private final BeerOrderRepository beerOrderRepository;
    private final CustomerRepository customerRepository;

    
	private final RoleRepository roleRepository;
	private final AuthorityRepository authorityRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
    	loadUserData();
        loadBreweryData();
        loadTastingRoomData();
        loadCustomers();
    }

    //customer je distributer piva koji nabavlja pivo iz brewery-ja.
    //takodje customer-u dodeljujemo usere, tj. svaki user moze biti 
    // odredjeni customer tako i distributer koji dobavlja
    private void loadCustomers() {
    	
    	val customerRole = roleRepository.findByName("CUSTOMER").orElseThrow();
    	
        val stPeteDistributingCustomer = Customer.builder()
                .customerName(ST_PETE_DISTRIBUTING)
                .apiKey(UUID.randomUUID())
                .build();
        
        val dunedinCustomer = Customer.builder()
                .customerName(DUNEDIN_DISTRIBUTING)
                .apiKey(UUID.randomUUID())
                .build();
        
        val keyWestCustomer = Customer.builder()
                .customerName(KEY_WEST_DISTRIBUTING)
                .apiKey(UUID.randomUUID())
                .build();
        
        customerRepository.saveAll(Arrays.asList(stPeteDistributingCustomer, dunedinCustomer, keyWestCustomer));
    	
		val stPeteUser = User.builder()
		         .username("stpete")
		         .password(passwordEncoder.encode("password"))
		         .role(customerRole)
		         .customer(stPeteDistributingCustomer)
		         .build();

        val dunedinUser = User.builder()
		     .username("dunedin")
		     .password(passwordEncoder.encode("password"))
		     .role(customerRole)
		     .customer(dunedinCustomer)
		     .build();

        val keyWestUser = User.builder()
	     .username("keywest")
	     .password(passwordEncoder.encode("password"))
	     .role(customerRole)
	     .customer(keyWestCustomer)
	     .build();
        
        userRepository.saveAll(Arrays.asList(stPeteUser, dunedinUser, keyWestUser));
        
        createOrder(stPeteDistributingCustomer);
        createOrder(dunedinCustomer);
        createOrder(keyWestCustomer);
        
	}
    
    private BeerOrder createOrder(Customer customer) {
    	return beerOrderRepository.save(BeerOrder.builder()
    			                   .customer(customer)
    			                   .orderStatus(OrderStatusEnum.NEW)
    			                   .beerOrderLines(Set.of(BeerOrderLine.builder()
    			                		                    .beer(beerRepository.findByUpc(BEER_1_UPC))
    			                		                    .orderQuantity(2)
    			                		                    .build()))
    			                   .build());
    			                   
    }

	private void loadUserData() {
    	if (userRepository.count() == 0) {

			val beerCreate = Authority.builder().permission("beer.create").build();
			val beerRead = Authority.builder().permission("beer.read").build();
			val beerUpdate = Authority.builder().permission("beer.update").build();
			val beerDelete = Authority.builder().permission("beer.delete").build();

			val customerCreate = Authority.builder().permission("customer.create").build();
			val customerRead = Authority.builder().permission("customer.read").build();
			val customerUpdate = Authority.builder().permission("customer.update").build();
			val customerDelete = Authority.builder().permission("customer.delete").build();

			val breweryCreate = Authority.builder().permission("brewery.create").build();
			val breweryRead = Authority.builder().permission("brewery.read").build();
			val breweryUpdate = Authority.builder().permission("brewery.update").build();
			val breweryDelete = Authority.builder().permission("brewery.delete").build();
			
			//admin user ce dobiti klasicne privilegije
			val orderCreate = Authority.builder().permission("order.create").build();
			val orderRead = Authority.builder().permission("order.read").build();
			val orderUpdate = Authority.builder().permission("order.update").build();
			val orderDelete = Authority.builder().permission("order.delete").build();
			val orderPickup = Authority.builder().permission("order.pickup").build();
			
			
			//customer ima svoje
			val customerOrderCreate = Authority.builder().permission("customer.order.create").build();
			val customerOrderRead = Authority.builder().permission("customer.order.read").build();
			val customerOrderUpdate = Authority.builder().permission("customer.order.update").build();
			val customerOrderDelete = Authority.builder().permission("customer.order.delete").build();
			val customerOrderPickup = Authority.builder().permission("customer.order.pickup").build();
			
			authorityRepository.saveAll(List.of(beerCreate, beerRead, beerUpdate, beerDelete,
                    breweryCreate, breweryRead, breweryUpdate, breweryDelete,
                    customerCreate, customerRead, customerUpdate, customerDelete,
                    orderCreate, orderRead, orderUpdate, orderDelete, orderPickup,
                    customerOrderCreate, customerOrderRead, customerOrderUpdate, customerOrderDelete,
                    customerOrderPickup));

			val adminRole = Role.builder().name("ADMIN")
					.authorities(new HashSet<>(Set.of(beerCreate, beerRead, beerUpdate, beerDelete,
							                          breweryCreate, breweryRead, breweryUpdate, breweryDelete,
							                          customerCreate, customerRead, customerUpdate, customerDelete,
							                          orderCreate, orderRead, orderUpdate, orderDelete, orderPickup)))
					                           .build();
			         //ne mozemo cist Set.of jer se hibernate buni kad cuva immutable kolekcije koje setujemo u 
			         //entitete.

			val userRole = Role.builder().name("USER").build();

			val customerRole = Role.builder().name("CUSTOMER")
					.authorities(new HashSet<>(Set.of(beerRead, customerRead, breweryRead,
							                          customerOrderCreate, customerOrderRead, customerOrderUpdate,
							                          customerOrderDelete, customerOrderPickup)))
					.build();

			roleRepository.saveAll(Arrays.asList(adminRole, userRole, customerRole));

			val springUser = User.builder().username("spring").password(passwordEncoder.encode("guru")).role(adminRole)
					.build();

			val user = User.builder().username("user").password(passwordEncoder.encode("password")).role(userRole)
					.build();

			val scottUser = User.builder().username("scott").password(passwordEncoder.encode("tiger"))
					.role(customerRole).build();

			userRepository.saveAll(Arrays.asList(springUser, user, scottUser));

			log.info("Users loaded: {}", userRepository.count());
		}
		
	}

	private void loadTastingRoomData() {
        Customer tastingRoom = Customer.builder()
                .customerName(TASTING_ROOM)
                .apiKey(UUID.randomUUID())
                .build();

        customerRepository.save(tastingRoom);

        beerRepository.findAll().forEach(beer -> {
            beerOrderRepository.save(BeerOrder.builder()
                    .customer(tastingRoom)
                    .orderStatus(OrderStatusEnum.NEW)
                    .beerOrderLines(Set.of(BeerOrderLine.builder()
                            .beer(beer)
                            .orderQuantity(2)
                            .build()))
                    .build());
        });
    }

    private void loadBreweryData() {
        if (breweryRepository.count() == 0) {
            breweryRepository.save(Brewery
                    .builder()
                    .breweryName("Cage Brewing")
                    .build());

            Beer mangoBobs = Beer.builder()
                    .beerName("Mango Bobs")
                    .beerStyle(BeerStyleEnum.IPA)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(BEER_1_UPC)
                    .build();

            beerRepository.save(mangoBobs);
            beerInventoryRepository.save(BeerInventory.builder()
                    .beer(mangoBobs)
                    .quantityOnHand(500)
                    .build());

            Beer galaxyCat = Beer.builder()
                    .beerName("Galaxy Cat")
                    .beerStyle(BeerStyleEnum.PALE_ALE)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(BEER_2_UPC)
                    .build();

            beerRepository.save(galaxyCat);
            beerInventoryRepository.save(BeerInventory.builder()
                    .beer(galaxyCat)
                    .quantityOnHand(500)
                    .build());

            Beer pinball = Beer.builder()
                    .beerName("Pinball Porter")
                    .beerStyle(BeerStyleEnum.PORTER)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(BEER_3_UPC)
                    .build();

            beerRepository.save(pinball);
            beerInventoryRepository.save(BeerInventory.builder()
                    .beer(pinball)
                    .quantityOnHand(500)
                    .build());

        }
    }
}
