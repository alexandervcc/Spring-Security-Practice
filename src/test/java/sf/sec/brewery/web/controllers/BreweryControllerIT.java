package sf.sec.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BreweryControllerIT  extends  BaseIT{
    @Test
    public void listBreweryAdmin() throws Exception {
        mockMvc.perform(get("/brewery/breweries")
                .with(httpBasic("spring","mana")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void listBreweryCustomer() throws Exception {
        mockMvc.perform(get("/brewery/breweries")
                        .with(httpBasic("doggo","mana")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void listBreweryUser() throws Exception {
        mockMvc.perform(get("/brewery/breweries")
                        .with(httpBasic("user","mana")))
                .andExpect(status().isForbidden());
    }
}
