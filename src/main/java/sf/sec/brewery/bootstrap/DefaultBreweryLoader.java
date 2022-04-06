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
package sf.sec.brewery.bootstrap;

import sf.sec.brewery.domain.*;
import sf.sec.brewery.domain.security.Authority;
import sf.sec.brewery.domain.security.Role;
import sf.sec.brewery.domain.security.User;
import sf.sec.brewery.repositories.security.AuthorityRepository;
import sf.sec.brewery.repositories.security.UserRepository;
import sf.sec.brewery.web.model.BeerStyleEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import sf.sec.brewery.repositories.*;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Component
public class DefaultBreweryLoader implements CommandLineRunner {

    public static final String ST_PETE_DIST = "St Pete Distribution";
    public static final String DUNEDIN_DIST = "Dunedin Distribution";
    public static final String KEYWEST_DIST = "Key West Distribution";
    public static final String ST_PETE_USER = "stpete";
    public static final String DUNEDIN_USER = "dunedin";
    public static final String KEYWEST_USER = "keywest";

    public static final String TASTING_ROOM = "Tasting Room";
    public static final String BEER_1_UPC = "0631234200036";
    public static final String BEER_2_UPC = "0631234300019";
    public static final String BEER_3_UPC = "0083783375213";

    private final BreweryRepository breweryRepository;
    private final BeerRepository beerRepository;
    private final BeerInventoryRepository beerInventoryRepository;
    private final BeerOrderRepository beerOrderRepository;
    private final CustomerRepository customerRepository;

    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        loadSecurityData();
        loadBreweryData();
        loadTastingRoom();
        loadCustomerData();
    }

    private void loadCustomerData() {
        Role customerRole = roleRepository.findByName("CUSTOMER").orElseThrow();

        //Creation of customers
        Customer stPeteCustomer = customerRepository.save(Customer.builder()
                .customerName(ST_PETE_DIST)
                .apiKey(UUID.randomUUID())
                .build());
        Customer dunedinCustomer = customerRepository.save(Customer.builder()
                .customerName(DUNEDIN_DIST)
                .apiKey(UUID.randomUUID())
                .build());
        Customer westCustomer = customerRepository.save(Customer.builder()
                .customerName(KEYWEST_DIST)
                .apiKey(UUID.randomUUID())
                .build());

        //Create Users
        User stPeteUser = userRepository.save(User.builder()
                .username(ST_PETE_USER)
                .password(passwordEncoder.encode("mana"))
                .customer(stPeteCustomer)
                .role(customerRole)
                .build());
        User dunemuUser = userRepository.save(User.builder()
                .username(DUNEDIN_USER).password(passwordEncoder.encode("mana"))
                .customer(dunedinCustomer)
                .role(customerRole)
                .build());
        User keywesUser = userRepository.save(User.builder()
                .username(KEYWEST_USER)
                .password(passwordEncoder.encode("mana"))
                .customer(westCustomer)
                .role(customerRole)
                .build());

        //Create Orders
        createOrder(westCustomer);
        createOrder(dunedinCustomer);
        createOrder(stPeteCustomer);

        log.debug("Orders Loaded: "+beerOrderRepository.count() );

    }

    private BeerOrder createOrder(Customer customer){
        return beerOrderRepository.save(
                BeerOrder.builder()
                        .customer(customer)
                        .orderStatus(OrderStatusEnum.NEW)
                        .beerOrderLines(Set.of(BeerOrderLine.builder()
                                .beer(beerRepository.findByUpc(BEER_1_UPC))
                                .orderQuantity(2)
                                .build()))
                        .build()
        );
    }
    private void loadTastingRoom() {
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

    private void loadSecurityData() {
        //Beer Auth
        Authority createBeer = authorityRepository.save(Authority.builder().permission("beer.create").build());
        Authority updateBeer = authorityRepository.save(Authority.builder().permission("beer.update").build());
        Authority deleteBeer = authorityRepository.save(Authority.builder().permission("beer.delete").build());
        Authority readBeer = authorityRepository.save(Authority.builder().permission("beer.read").build());

        //Orders Authorities - Admin
        Authority createOrder = authorityRepository.save(Authority.builder().permission("order.create").build());
        Authority updateOrder = authorityRepository.save(Authority.builder().permission("order.update").build());
        Authority deleteOrder = authorityRepository.save(Authority.builder().permission("order.delete").build());
        Authority readOrder = authorityRepository.save(Authority.builder().permission("order.read").build());

        //Customer Auhtorities
        Authority readCustomer = authorityRepository.save(Authority.builder().permission("customer.read").build());
        Authority createCustomer = authorityRepository.save(Authority.builder().permission("customer.create").build());

        //Orders Authorities - Customer
        Authority createOrderCustom =
                authorityRepository.save(Authority.builder().permission("customer.order.create").build());
        Authority updateOrderCustom =
                authorityRepository.save(Authority.builder().permission("customer.order.update").build());
        Authority deleteOrderCustom =
                authorityRepository.save(Authority.builder().permission("customer.order.delete").build());
        Authority readOrderCustom =
                authorityRepository.save(Authority.builder().permission("customer.order.read").build());


        Role roleAdmin = roleRepository.save(Role.builder().name("ADMIN").build());
        Role roleCusto = roleRepository.save(Role.builder().name("CUSTOMER").build());
        Role roleUser = roleRepository.save(Role.builder().name("USER").build());

        roleAdmin.setAuthorities(Set.of(
                createBeer, readBeer, updateBeer, deleteBeer,
                createOrder, readOrder, deleteOrder, updateOrder,
                readCustomer, createCustomer
        ));
        roleCusto.setAuthorities(Set.of(
                readBeer,
                readOrderCustom, createOrderCustom, deleteOrderCustom, updateOrderCustom,
                readCustomer
        ));
        roleUser.setAuthorities(Set.of(readBeer));

        roleRepository.saveAll(Arrays.asList(roleAdmin, roleUser, roleCusto));

        userRepository.save(User.builder()
                .username("spring")
                .password(passwordEncoder.encode("mana"))
                .role(roleAdmin)
                .build());

        userRepository.save(User.builder()
                .username("doggo")
                .password(passwordEncoder.encode("mana"))
                .role(roleCusto)
                .build());

        userRepository.save(User.builder()
                .username("user")
                .password(passwordEncoder.encode("mana"))
                .role(roleUser)
                .build());

        log.debug("Users Loaded: " + userRepository.count());
    }

}
