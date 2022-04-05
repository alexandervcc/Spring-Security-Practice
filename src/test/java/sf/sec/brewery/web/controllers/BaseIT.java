package sf.sec.brewery.web.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

/**
 * Created by jt on 6/13/20.
 */
public abstract class BaseIT {
    @Autowired
    WebApplicationContext wac;

    protected MockMvc mockMvc;

//    @MockBean
//    BeerRepository beerRepository;
//
//    @MockBean
//    BeerInventoryRepository beerInventoryRepository;
//
//    @MockBean
//    BreweryService breweryService;
//
//    @MockBean
//    CustomerRepository customerRepository;
//
//    @MockBean
//    BeerService beerService;
//
//    @MockBean
//    BeerOrderService beerOrderService;


    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    public static Stream<Arguments> getStreamAdminCustomer(){
        return Stream.of(
                Arguments.of("spring","mana"),
                Arguments.of("doggo","mana")
        );
    }
    public static Stream<Arguments> getStreamAllUsers(){
        return Stream.of(
                Arguments.of("spring","mana"),
                Arguments.of("user","mana"),
                Arguments.of("doggo","mana")
        );
    }
    public static Stream<Arguments> getStreamNotAdmin(){
        return Stream.of(
                Arguments.of("user","mana"),
                Arguments.of("doggo","mana")
        );
    }

}
