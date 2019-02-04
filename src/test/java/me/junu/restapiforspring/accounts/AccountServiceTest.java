package me.junu.restapiforspring.accounts;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void loadUserByUsername(){
        //Given
        String email = "junwoo@naver.com";
        String password = "junwoo";
        Account account = Account.builder()
                .email(email)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        accountService.save(account);

        //When
        UserDetailsService userDetailsService = accountService;
        UserDetails junwoo = userDetailsService.loadUserByUsername(email);

        //Then
        assertThat(junwoo.getUsername()).isEqualTo(email);
        assertThat(passwordEncoder.matches(password, junwoo.getPassword()));
    }

    @Test
    public void loadUserByUsernameFail(){
        String email = "random@paran.com";

        try{
            accountService.loadUserByUsername(email);
            fail("supposed to be found exception");
        } catch(UsernameNotFoundException e ){
            assertThat(e.getMessage()).containsSequence(email);
        }
    }
}