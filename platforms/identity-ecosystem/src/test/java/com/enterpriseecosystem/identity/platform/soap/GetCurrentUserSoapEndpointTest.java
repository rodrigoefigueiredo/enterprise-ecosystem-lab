package com.enterpriseecosystem.identity.platform.soap;

import java.util.Arrays;

import org.junit.After;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.w3c.dom.Element;

import com.enterpriseecosystem.identity.authentication.IdentityUserDetails;
import com.enterpriseecosystem.identity.identity.User;
import com.enterpriseecosystem.identity.identity.UserDao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetCurrentUserSoapEndpointTest {

    @After
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void returnsCurrentAuthenticatedUser() {
        UserDao userDao = mock(UserDao.class);
        User user = new User();
        user.setPublicId("public-id-1");
        user.setEmail("admin@example.com");
        user.setDisplayName("Admin");
        user.setStatus("ACTIVE");
        when(userDao.findByPublicId("public-id-1")).thenReturn(user);
        authenticate();

        GetCurrentUserSoapEndpoint endpoint = new GetCurrentUserSoapEndpoint(userDao);

        Element response = endpoint.getCurrentUser();

        assertThat(text(response, "publicId"), is("public-id-1"));
        assertThat(text(response, "email"), is("admin@example.com"));
        assertThat(text(response, "displayName"), is("Admin"));
        assertThat(text(response, "status"), is("ACTIVE"));
        assertThat(text(response, "authority"), is("ROLE_ADMIN"));
    }

    @Test(expected = GetCurrentUserSoapException.class)
    public void rejectsMissingAuthentication() {
        GetCurrentUserSoapEndpoint endpoint = new GetCurrentUserSoapEndpoint(mock(UserDao.class));

        endpoint.getCurrentUser();
    }

    @Test(expected = GetCurrentUserSoapException.class)
    public void rejectsMissingUserRecord() {
        UserDao userDao = mock(UserDao.class);
        when(userDao.findByPublicId("public-id-1")).thenReturn(null);
        authenticate();
        GetCurrentUserSoapEndpoint endpoint = new GetCurrentUserSoapEndpoint(userDao);

        endpoint.getCurrentUser();
    }

    private void authenticate() {
        GrantedAuthority authority = new GrantedAuthorityImpl("ROLE_ADMIN");
        IdentityUserDetails principal = new IdentityUserDetails(
                "public-id-1",
                "admin@example.com",
                "hash",
                true,
                Arrays.asList(authority));
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, "hash", principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String text(Element parent, String localName) {
        return parent.getElementsByTagNameNS(GetCurrentUserSoapEndpoint.NAMESPACE_URI, localName)
                .item(0)
                .getTextContent();
    }
}
