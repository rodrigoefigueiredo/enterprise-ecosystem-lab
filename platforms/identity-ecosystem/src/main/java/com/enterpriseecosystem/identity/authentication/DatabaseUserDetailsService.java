package com.enterpriseecosystem.identity.authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.enterpriseecosystem.identity.credential.PasswordCredential;
import com.enterpriseecosystem.identity.credential.PasswordCredentialDao;
import com.enterpriseecosystem.identity.identity.User;
import com.enterpriseecosystem.identity.identity.UserDao;

@Service("databaseUserDetailsService")
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UserDao userDao;
    private final PasswordCredentialDao passwordCredentialDao;

    @Autowired
    public DatabaseUserDetailsService(UserDao userDao, PasswordCredentialDao passwordCredentialDao) {
        this.userDao = userDao;
        this.passwordCredentialDao = passwordCredentialDao;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String email = username.trim().toLowerCase(Locale.US);
        User user = userDao.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        PasswordCredential credential = passwordCredentialDao.findActiveByUserId(user.getId());
        if (credential == null) {
            throw new UsernameNotFoundException("Active credential not found");
        }

        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new GrantedAuthorityImpl("ROLE_USER"));

        return new IdentityUserDetails(
                user.getPublicId(),
                user.getEmail(),
                credential.getPasswordHash(),
                "ACTIVE".equals(user.getStatus()),
                authorities);
    }
}
