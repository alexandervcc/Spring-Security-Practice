package sf.sec.brewery.web.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class CustomerControllerIT  extends BaseIT{

    @ParameterizedTest(name="#{index} with #[{arguments}]")
    @MethodSource("sf.sec.brewery.web.controllers.BeerControllerIT#getStreamAdminCustomer")
    void testListCustomersAuth(String user, String pass) throws Exception{
        mockMvc.perform(get("/customers").with(csrf())
                .with(httpBasic(user,pass)))
                .andExpect(status().isOk());
    }

    @Test
    void testListCustomersNoAuth() throws Exception {
        mockMvc.perform(get("/customers").with(csrf())
                .with(httpBasic("user","mana")))
                .andExpect(status().isForbidden());
    }

    @Test
    void testListCustomersNotLogIn() throws Exception {
        mockMvc.perform(get("/customers").with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("Add customers")
    @Nested
    class AddCustomers{
        @Rollback
        @Test
        void processCreationForm() throws Exception{
            mockMvc.perform(post("/customers/new").with(csrf())
                    .param("customerName","ManasesesClient")
                    .with(httpBasic("spring","mana")))
                    .andExpect(status().is3xxRedirection());
        }

        @Rollback
        @ParameterizedTest(name="#{index} with [#{arguments}]")
        @MethodSource("sf.sec.brewery.web.controllers.BeerControllerIT#getStreamNotAdmin")
        void processCreationFormNOTAuth(String user, String pass) throws Exception{
            mockMvc.perform(post("/customers/new").with(csrf())
                    .param("customerName","MijotronClient")
                    .with(httpBasic(user,pass)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void processCreationFormNoAuth() throws Exception{
            mockMvc.perform(post("/customers/new").with(csrf())
                    .param("customerName","CuicochasClient"))
                    .andExpect(status().isUnauthorized());
        }
    }
}
