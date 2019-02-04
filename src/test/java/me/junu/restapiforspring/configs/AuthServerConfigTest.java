package me.junu.restapiforspring.configs;

import me.junu.restapiforspring.accounts.Account;
import me.junu.restapiforspring.accounts.AccountRole;
import me.junu.restapiforspring.accounts.AccountService;
import me.junu.restapiforspring.common.BaseControllerTest;
import me.junu.restapiforspring.common.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

    @Test
    @TestDescription("인증토큰 발급받는 테스트")
    public void getAuthToken() throws Exception {
        //Given
        Account account = Account.builder()
                .email("junwoo@naver.com")
                .password("password")
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        accountService.save(account);


        String clientId = "myapp";
        String clientSecret = "pass";
        mockMvc.perform(post("/oauth/token")
                .with(httpBasic(clientId, clientSecret))
                .param("username", "junwoo @naver.com")
                .param("password", "password")
                .param("grant_type", "password")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists());

    }
}