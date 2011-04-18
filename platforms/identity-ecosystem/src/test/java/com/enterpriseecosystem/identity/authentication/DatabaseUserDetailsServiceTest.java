package com.enterpriseecosystem.identity.authentication;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.enterpriseecosystem.identity.credential.PasswordCredential;
import com.enterpriseecosystem.identity.credential.PasswordCredentialDao;
import com.enterpriseecosystem.identity.identity.User;
import com.enterpriseecosystem.identity.identity.UserAuthorityDao;
import com.enterpriseecosystem.identity.identity.UserDao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DatabaseUserDetailsServiceTest {

    @Test
    public void loadsAuthoritiesFromDatabase() {
        User user = new User();
        user.setPublicId("public-id");
        user.setEmail("admin@example.com");
        user.setStatus("ACTIVE");

        PasswordCredential credential = new PasswordCredential();
        credential.setPasswordHash("stored-hash");

        UserDao userDao = mock(UserDao.class);
        PasswordCredentialDao credentialDao = mock(PasswordCredentialDao.class);
        UserAuthorityDao authorityDao = mock(UserAuthorityDao.class);
        when(userDao.findByEmail("admin@example.com")).thenReturn(user);
        when(credentialDao.findActiveByUserId(null)).thenReturn(credential);
        when(authorityDao.findByUserId(null)).thenReturn(Arrays.asList("ROLE_ADMIN", "ROLE_USER"));

        DatabaseUserDetailsService service = new DatabaseUserDetailsService(userDao, credentialDao, authorityDao);

        UserDetails details = service.loadUserByUsername(" Admin@Example.com ");

        assertThat(details.getUsername(), is("admin@example.com"));
        assertThat(details.getPassword(), is("stored-hash"));
        assertThat(hasAuthority(details.getAuthorities(), "ROLE_ADMIN"), is(true));
        assertThat(hasAuthority(details.getAuthorities(), "ROLE_USER"), is(true));
    }

    @Test(expected = UsernameNotFoundException.class)
    public void rejectsUserWithoutAuthorities() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setStatus("ACTIVE");

        PasswordCredential credential = new PasswordCredential();
        credential.setPasswordHash("stored-hash");

        UserDao userDao = mock(UserDao.class);
        PasswordCredentialDao credentialDao = mock(PasswordCredentialDao.class);
        UserAuthorityDao authorityDao = mock(UserAuthorityDao.class);
        when(userDao.findByEmail("user@example.com")).thenReturn(user);
        when(credentialDao.findActiveByUserId(null)).thenReturn(credential);
        when(authorityDao.findByUserId(null)).thenReturn(Arrays.<String>asList());

        DatabaseUserDetailsService service = new DatabaseUserDetailsService(userDao, credentialDao, authorityDao);

        service.loadUserByUsername("user@example.com");
    }

    private boolean hasAuthority(Collection<GrantedAuthority> authorities, String authority) {
        for (GrantedAuthority grantedAuthority : authorities) {
            if (authority.equals(grantedAuthority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
