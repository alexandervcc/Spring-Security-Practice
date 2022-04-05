package sf.sec.brewery.bootstrap;

import sf.sec.brewery.domain.security.Authority;
import sf.sec.brewery.domain.security.Role;
import sf.sec.brewery.domain.security.User;
import sf.sec.brewery.repositories.RoleRepository;
import sf.sec.brewery.repositories.security.AuthorityRepository;
import sf.sec.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Set;


@Slf4j
@RequiredArgsConstructor
//@Component
public class UserDataLoader implements CommandLineRunner {

    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

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
        Role roleCusto = roleRepository.save(Role.builder().name("COSTUMER").build());
        Role roleUser  = roleRepository.save(Role.builder().name("USER").build());

        roleAdmin.setAuthorities(Set.of(
                createBeer,readBeer,updateBeer,deleteBeer, createOrder,readOrder, deleteOrder, updateOrder
        ));
        roleCusto.setAuthorities(Set.of(
                readBeer, readOrderCustom,createOrderCustom,deleteOrderCustom,updateOrderCustom
        ));
        roleUser.setAuthorities(Set.of(readBeer));

        roleRepository.saveAll(Arrays.asList(roleAdmin,roleUser,roleCusto));

        userRepository.save(User.builder()
                .username("spring")
                .password(passwordEncoder.encode("mana"))
                .role(roleAdmin)
                .build());

        userRepository.save(User.builder()
                .username("user")
                .password(passwordEncoder.encode("mana"))
                .role(roleCusto)
                .build());

        userRepository.save(User.builder()
                .username("doggo")
                .password(passwordEncoder.encode("mana"))
                .role(roleUser)
                .build());

        log.debug("Users Loaded: " + userRepository.count());
    }

    @Override
    public void run(String... args) throws Exception {
//        if (authorityRepository.count() == 0) {
//            loadSecurityData();
//        }
    }


}
