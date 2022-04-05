package sf.sec.brewery.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import sf.sec.brewery.bootstrap.DefaultBreweryLoader;
import sf.sec.brewery.domain.Beer;
import sf.sec.brewery.domain.Customer;
import sf.sec.brewery.repositories.BeerOrderRepository;
import sf.sec.brewery.repositories.BeerRepository;
import sf.sec.brewery.repositories.CustomerRepository;
import sf.sec.brewery.web.model.BeerOrderDto;
import sf.sec.brewery.web.model.BeerOrderLineDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
public class BeerOrderControllerTest extends BaseIT{
    public static final String  API_ROOT = "/api/v1/customers/";

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    ObjectMapper objectMapper;

    Customer stpeteCustomer;
    Customer dunedinCustomer;
    Customer keyWestCustomer;

    List<Beer> loadedBeers;

    @BeforeEach
    void setCustomers(){
        stpeteCustomer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.ST_PETE_DIST).orElseThrow();
        dunedinCustomer= customerRepository.findAllByCustomerName(DefaultBreweryLoader.DUNEDIN_DIST).orElseThrow();
        keyWestCustomer= customerRepository.findAllByCustomerName(DefaultBreweryLoader.KEYWEST_DIST).orElseThrow();
        loadedBeers = beerRepository.findAll();
    }

    @Test
    void createOrderNoAuth() throws Exception{
        BeerOrderDto beerOrderDto = buildOrderDTO(stpeteCustomer,loadedBeers.get(0).getId());

        mockMvc.perform(post(API_ROOT+stpeteCustomer.getId()+"/orders")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails("spring")
    @Test
    void createOrderUserAdmin() throws Exception{
        BeerOrderDto beerOrderDto = buildOrderDTO(stpeteCustomer,loadedBeers.get(0).getId());

        mockMvc.perform(post(API_ROOT+stpeteCustomer.getId()+"/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(status().isCreated());
    }

    @WithUserDetails(DefaultBreweryLoader.ST_PETE_USER)
    @Test
    void createOrderUserAuthCustomer() throws Exception{
        BeerOrderDto beerOrderDto = buildOrderDTO(stpeteCustomer,loadedBeers.get(0).getId());

        mockMvc.perform(post(API_ROOT+stpeteCustomer.getId()+"/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(status().isCreated());
    }

    @WithUserDetails(DefaultBreweryLoader.KEYWEST_USER)
    @Test
    void createOrderUserNOTAuthCustomer() throws Exception{
        BeerOrderDto beerOrderDto = buildOrderDTO(stpeteCustomer,loadedBeers.get(0).getId());

        mockMvc.perform(post(API_ROOT+stpeteCustomer.getId()+"/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void listOrdersNotAuth() throws Exception{
        mockMvc.perform(get(API_ROOT+stpeteCustomer.getId()+"/orders"))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails(value = "spring")
    @Test
    void listOrdersAdminAuth() throws Exception{
        mockMvc.perform(get(API_ROOT+stpeteCustomer.getId()+"/orders"))
                .andExpect(status().isOk());
    }

    @WithUserDetails(value = DefaultBreweryLoader.ST_PETE_USER)
    @Test
    void listOrdersCustomerAuth() throws Exception{
        mockMvc.perform(get(API_ROOT+stpeteCustomer.getId()+"/orders"))
                .andExpect(status().isOk());
    }

    @WithUserDetails(value = DefaultBreweryLoader.DUNEDIN_USER)
    @Test
    void listOrdersCustomerNOTAuth() throws Exception{
        mockMvc.perform(get(API_ROOT+stpeteCustomer.getId()+"/orders"))
                .andExpect(status().isForbidden());
    }

    @Test
    void listOrdersNoAuth() throws Exception{
        mockMvc.perform(get(API_ROOT+stpeteCustomer.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Disabled
    @Test
    void pickUpOrderNotAuth(){

    }

    @Disabled
    @Test
    void pickUpOrderNotAdminUser(){

    }

    @Disabled
    @Test
    void pickUpOrderCustomerUserAuth(){

    }

    @Disabled
    @Test
    void pickUpOrderCustomerUserNotAuth(){

    }

    private BeerOrderDto buildOrderDTO(Customer customer, UUID beerId){
        List<BeerOrderLineDto> orderLines = Arrays.asList(BeerOrderLineDto.builder()
                .id(UUID.randomUUID())
                .beerId(beerId)
                .orderQuantity(5)
                .build());

        return BeerOrderDto.builder()
                .customerId(customer.getId())
                .customerRef("123")
                .orderStatusCallbackUrl("http://example.com")
                .beerOrderLines(orderLines)
                .build();
    }
}
