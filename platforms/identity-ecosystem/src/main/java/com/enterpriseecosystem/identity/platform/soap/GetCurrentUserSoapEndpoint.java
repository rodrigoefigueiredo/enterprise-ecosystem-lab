package com.enterpriseecosystem.identity.platform.soap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enterpriseecosystem.identity.authentication.IdentityUserDetails;
import com.enterpriseecosystem.identity.identity.User;
import com.enterpriseecosystem.identity.identity.UserDao;

@Endpoint
public class GetCurrentUserSoapEndpoint {

    static final String NAMESPACE_URI = "http://enterpriseecosystem.com/identity/ws";

    private final UserDao userDao;

    @Autowired
    public GetCurrentUserSoapEndpoint(UserDao userDao) {
        this.userDao = userDao;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetCurrentUserRequest")
    @ResponsePayload
    public Element getCurrentUser() {
        IdentityUserDetails userDetails = currentUserDetails();
        User user = userDao.findByPublicId(userDetails.getPublicId());
        if (user == null) {
            throw new GetCurrentUserSoapException("Current user could not be resolved.");
        }

        return response(user, userDetails.getAuthorities());
    }

    private IdentityUserDetails currentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new GetCurrentUserSoapException("Current user could not be resolved.");
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof IdentityUserDetails)) {
            throw new GetCurrentUserSoapException("Current user could not be resolved.");
        }
        return (IdentityUserDetails) principal;
    }

    private Element response(User user, Collection<GrantedAuthority> grantedAuthorities) {
        Document document = newDocument();
        Element response = document.createElementNS(NAMESPACE_URI, "identity:GetCurrentUserResponse");
        response.setAttribute("xmlns:identity", NAMESPACE_URI);
        document.appendChild(response);

        append(document, response, "publicId", user.getPublicId());
        append(document, response, "email", user.getEmail());
        append(document, response, "displayName", user.getDisplayName());
        append(document, response, "status", user.getStatus());

        Element authorities = document.createElementNS(NAMESPACE_URI, "identity:authorities");
        response.appendChild(authorities);
        for (String authority : authorityNames(grantedAuthorities)) {
            append(document, authorities, "authority", authority);
        }

        return response;
    }

    private List<String> authorityNames(Collection<GrantedAuthority> grantedAuthorities) {
        List<String> names = new ArrayList<String>();
        for (GrantedAuthority grantedAuthority : grantedAuthorities) {
            names.add(grantedAuthority.getAuthority());
        }
        Collections.sort(names);
        return names;
    }

    private Document newDocument() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            return factory.newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("Could not create SOAP response document.", e);
        }
    }

    private void append(Document document, Element parent, String name, String value) {
        Element child = document.createElementNS(NAMESPACE_URI, "identity:" + name);
        child.appendChild(document.createTextNode(value));
        parent.appendChild(child);
    }
}
