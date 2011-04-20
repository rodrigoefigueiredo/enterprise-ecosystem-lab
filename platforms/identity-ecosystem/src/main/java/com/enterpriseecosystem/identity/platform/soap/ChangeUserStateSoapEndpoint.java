package com.enterpriseecosystem.identity.platform.soap;

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

import com.enterpriseecosystem.identity.identity.ChangeUserStateRequest;
import com.enterpriseecosystem.identity.identity.ChangeUserStateUseCase;
import com.enterpriseecosystem.identity.identity.InvalidUserStateChangeException;
import com.enterpriseecosystem.identity.identity.User;
import com.enterpriseecosystem.identity.identity.UserNotFoundException;

@Endpoint
public class ChangeUserStateSoapEndpoint {

    static final String NAMESPACE_URI = "http://enterpriseecosystem.com/identity/ws";

    private final ChangeUserStateUseCase changeUserStateUseCase;

    @Autowired
    public ChangeUserStateSoapEndpoint(ChangeUserStateUseCase changeUserStateUseCase) {
        this.changeUserStateUseCase = changeUserStateUseCase;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ChangeUserStateRequest")
    @ResponsePayload
    public Element changeUserState(@RequestPayload Element requestElement) {
        String publicId = text(requestElement, "publicId");
        String status = text(requestElement, "status");

        try {
            User user = changeUserStateUseCase.changeState(new ChangeUserStateRequest(publicId, status));
            return response(user);
        } catch (InvalidUserStateChangeException e) {
            throw new ChangeUserStateSoapException("User state could not be changed.");
        } catch (UserNotFoundException e) {
            throw new ChangeUserStateSoapException("User state could not be changed.");
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
        Element response = document.createElementNS(NAMESPACE_URI, "identity:ChangeUserStateResponse");
        response.setAttribute("xmlns:identity", NAMESPACE_URI);
        document.appendChild(response);

        append(document, response, "publicId", user.getPublicId());
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
}
