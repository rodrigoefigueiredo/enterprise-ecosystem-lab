package com.enterpriseecosystem.identity.platform.soap;

import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.enterpriseecosystem.identity.identity.CreateUserRequest;
import com.enterpriseecosystem.identity.identity.CreateUserUseCase;
import com.enterpriseecosystem.identity.identity.DuplicateEmailException;
import com.enterpriseecosystem.identity.identity.User;

@Endpoint
public class CreateUserSoapEndpoint {

    static final String NAMESPACE_URI = "http://enterpriseecosystem.com/identity/ws";

    private static final int MINIMUM_PASSWORD_LENGTH = 8;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final CreateUserUseCase createUserUseCase;

    @Autowired
    public CreateUserSoapEndpoint(CreateUserUseCase createUserUseCase) {
        this.createUserUseCase = createUserUseCase;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CreateUserRequest")
    @ResponsePayload
    public Element createUser(@RequestPayload Element requestElement) {
        String email = text(requestElement, "email");
        String displayName = text(requestElement, "displayName");
        String password = text(requestElement, "password");

        validate(email, displayName, password);

        try {
            User user = createUserUseCase.create(new CreateUserRequest(email, displayName, password));
            return response(user);
        } catch (DuplicateEmailException e) {
            throw new CreateUserSoapException("User could not be created.");
        }
    }

    private void validate(String email, String displayName, String password) {
        if (isBlank(email) || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new CreateUserSoapException("User could not be created.");
        }
        if (isBlank(displayName)) {
            throw new CreateUserSoapException("User could not be created.");
        }
        if (isBlank(password) || password.length() < MINIMUM_PASSWORD_LENGTH) {
            throw new CreateUserSoapException("User could not be created.");
        }
    }

    private String text(Element parent, String localName) {
        NodeList namespacedElements = parent.getElementsByTagNameNS(NAMESPACE_URI, localName);
        if (namespacedElements.getLength() > 0) {
            return namespacedElements.item(0).getTextContent();
        }

        NodeList elements = parent.getElementsByTagName(localName);
        if (elements.getLength() > 0) {
            return elements.item(0).getTextContent();
        }
        return null;
    }

    private Element response(User user) {
        Document document = newDocument();
        Element response = document.createElementNS(NAMESPACE_URI, "identity:CreateUserResponse");
        response.setAttribute("xmlns:identity", NAMESPACE_URI);
        document.appendChild(response);

        append(document, response, "publicId", user.getPublicId());
        append(document, response, "email", user.getEmail());
        append(document, response, "displayName", user.getDisplayName());
        append(document, response, "status", user.getStatus());

        return response;
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

    private boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }
}
