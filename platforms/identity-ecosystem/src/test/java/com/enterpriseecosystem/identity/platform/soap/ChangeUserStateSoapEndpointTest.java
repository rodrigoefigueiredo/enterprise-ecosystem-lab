package com.enterpriseecosystem.identity.platform.soap;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enterpriseecosystem.identity.identity.ChangeUserStateRequest;
import com.enterpriseecosystem.identity.identity.ChangeUserStateUseCase;
import com.enterpriseecosystem.identity.identity.InvalidUserStateChangeException;
import com.enterpriseecosystem.identity.identity.User;
import com.enterpriseecosystem.identity.identity.UserNotFoundException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChangeUserStateSoapEndpointTest {

    @Test
    public void changesUserStateFromSoapRequest() throws Exception {
        ChangeUserStateUseCase useCase = mock(ChangeUserStateUseCase.class);
        User user = new User();
        user.setPublicId("public-id-1");
        user.setStatus("LOCKED");
        when(useCase.changeState(org.mockito.Matchers.any(ChangeUserStateRequest.class))).thenReturn(user);

        ChangeUserStateSoapEndpoint endpoint = new ChangeUserStateSoapEndpoint(useCase);

        Element response = endpoint.changeUserState(request("public-id-1", "LOCKED"));

        ArgumentCaptor<ChangeUserStateRequest> requestCaptor = ArgumentCaptor.forClass(ChangeUserStateRequest.class);
        verify(useCase).changeState(requestCaptor.capture());
        assertThat(requestCaptor.getValue().getPublicId(), is("public-id-1"));
        assertThat(requestCaptor.getValue().getStatus(), is("LOCKED"));
        assertThat(text(response, "publicId"), is("public-id-1"));
        assertThat(text(response, "status"), is("LOCKED"));
    }

    @Test(expected = ChangeUserStateSoapException.class)
    public void mapsInvalidStateToGenericSoapFailure() throws Exception {
        ChangeUserStateUseCase useCase = mock(ChangeUserStateUseCase.class);
        when(useCase.changeState(org.mockito.Matchers.any(ChangeUserStateRequest.class)))
                .thenThrow(new InvalidUserStateChangeException());
        ChangeUserStateSoapEndpoint endpoint = new ChangeUserStateSoapEndpoint(useCase);

        endpoint.changeUserState(request("public-id-1", "DISABLED"));
    }

    @Test(expected = ChangeUserStateSoapException.class)
    public void mapsUnknownUserToGenericSoapFailure() throws Exception {
        ChangeUserStateUseCase useCase = mock(ChangeUserStateUseCase.class);
        when(useCase.changeState(org.mockito.Matchers.any(ChangeUserStateRequest.class)))
                .thenThrow(new UserNotFoundException("missing-id"));
        ChangeUserStateSoapEndpoint endpoint = new ChangeUserStateSoapEndpoint(useCase);

        endpoint.changeUserState(request("missing-id", "LOCKED"));
    }

    private Element request(String publicId, String status) throws Exception {
        String xml =
                "<identity:ChangeUserStateRequest xmlns:identity=\"" + ChangeUserStateSoapEndpoint.NAMESPACE_URI + "\">" +
                "<identity:publicId>" + publicId + "</identity:publicId>" +
                "<identity:status>" + status + "</identity:status>" +
                "</identity:ChangeUserStateRequest>";

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document document = factory.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        return document.getDocumentElement();
    }

    private String text(Element parent, String localName) {
        return parent.getElementsByTagNameNS(ChangeUserStateSoapEndpoint.NAMESPACE_URI, localName)
                .item(0)
                .getTextContent();
    }
}
