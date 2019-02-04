package me.junu.restapiforspring.configs;

import me.junu.restapiforspring.accounts.Account;
import me.junu.restapiforspring.accounts.AccountRole;
import me.junu.restapiforspring.accounts.AccountService;
import me.junu.restapiforspring.common.AppProperties;
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
    @Autowired
    AppProperties appProperties;

    @Test
    @TestDescription("인증토큰 발급받는 테스트")
    public void getAuthToken() throws Exception {



        mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
                .param("username", appProperties.getUserUsername())
                .param("password", appProperties.getUserPassword())
                .param("grant_type", "password")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists());

    }
}