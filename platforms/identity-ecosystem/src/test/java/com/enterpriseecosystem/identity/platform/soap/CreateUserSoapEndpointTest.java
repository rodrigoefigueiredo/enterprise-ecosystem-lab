package com.enterpriseecosystem.identity.platform.soap;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enterpriseecosystem.identity.identity.CreateUserRequest;
import com.enterpriseecosystem.identity.identity.CreateUserUseCase;
import com.enterpriseecosystem.identity.identity.DuplicateEmailException;
import com.enterpriseecosystem.identity.identity.User;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateUserSoapEndpointTest {

    @Test
    public void createsUserFromSoapRequest() throws Exception {
        CreateUserUseCase useCase = mock(CreateUserUseCase.class);
        User user = new User();
        user.setPublicId("public-id-1");
        user.setEmail("alice@example.com");
        user.setDisplayName("Alice");
        user.setStatus("ACTIVE");
        when(useCase.create(org.mockito.Matchers.any(CreateUserRequest.class))).thenReturn(user);

        CreateUserSoapEndpoint endpoint = new CreateUserSoapEndpoint(useCase);

        Element response = endpoint.createUser(request(
                "alice@example.com",
                "Alice",
                "changeit123"));

        ArgumentCaptor<CreateUserRequest> requestCaptor = ArgumentCaptor.forClass(CreateUserRequest.class);
        verify(useCase).create(requestCaptor.capture());
        assertThat(requestCaptor.getValue().getEmail(), is("alice@example.com"));
        assertThat(requestCaptor.getValue().getDisplayName(), is("Alice"));
        assertThat(requestCaptor.getValue().getPassword(), is("changeit123"));
        assertThat(text(response, "publicId"), is("public-id-1"));
        assertThat(text(response, "email"), is("alice@example.com"));
        assertThat(text(response, "displayName"), is("Alice"));
        assertThat(text(response, "status"), is("ACTIVE"));
    }

    @Test(expected = CreateUserSoapException.class)
    public void rejectsInvalidRequest() throws Exception {
        CreateUserSoapEndpoint endpoint = new CreateUserSoapEndpoint(mock(CreateUserUseCase.class));

        endpoint.createUser(request("not-an-email", "Alice", "short"));
    }

    @Test(expected = CreateUserSoapException.class)
    public void mapsDuplicateEmailToGenericSoapFailure() throws Exception {
        CreateUserUseCase useCase = mock(CreateUserUseCase.class);
        when(useCase.create(org.mockito.Matchers.any(CreateUserRequest.class)))
                .thenThrow(new DuplicateEmailException("alice@example.com"));
        CreateUserSoapEndpoint endpoint = new CreateUserSoapEndpoint(useCase);

        endpoint.createUser(request("alice@example.com", "Alice", "changeit123"));
    }

    private Element request(String email, String displayName, String password) throws Exception {
        String xml =
                "<identity:CreateUserRequest xmlns:identity=\"" + CreateUserSoapEndpoint.NAMESPACE_URI + "\">" +
                "<identity:email>" + email + "</identity:email>" +
                "<identity:displayName>" + displayName + "</identity:displayName>" +
                "<identity:password>" + password + "</identity:password>" +
                "</identity:CreateUserRequest>";

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document document = factory.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        return document.getDocumentElement();
    }

    private String text(Element parent, String localName) {
        return parent.getElementsByTagNameNS(CreateUserSoapEndpoint.NAMESPACE_URI, localName)
                .item(0)
                .getTextContent();
    }
}
