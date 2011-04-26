package com.enterpriseecosystem.identity.platform.web;

import com.enterpriseecosystem.identity.authentication.IdentityUserDetails;
import com.enterpriseecosystem.identity.credential.ChangePasswordRequest;
import com.enterpriseecosystem.identity.credential.CredentialManagementUseCase;
import com.enterpriseecosystem.identity.credential.InvalidCurrentPasswordException;
import com.enterpriseecosystem.identity.credential.PasswordPolicy;
import java.util.Arrays;
import java.util.Collection;
import org.junit.After;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BindingResult;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

public class CredentialControllerTest {

    @After
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void changePasswordReturnsFormView() {
        CredentialController controller = newController();
        ExtendedModelMap model = new ExtendedModelMap();

        String viewName = controller.changePassword(model);

        assertThat(viewName, is("me/password"));
        assertThat(model.containsAttribute("changePasswordForm"), is(true));
        assertThat(model.get("changePasswordForm"), instanceOf(ChangePasswordForm.class));
    }

    @Test
    public void changePasswordChangesAuthenticatedUsersPassword() {
        CredentialManagementUseCase useCase = mock(CredentialManagementUseCase.class);
        CredentialController controller = new CredentialController(useCase, new PasswordPolicy());
        authenticateAs("user-public-id");
        ChangePasswordForm form = form("current-password", "new-password", "new-password");
        ExtendedModelMap model = new ExtendedModelMap();
        BindingResult bindingResult = bindingResult(form);

        String viewName = controller.changePassword(form, bindingResult, model);

        assertThat(viewName, is("me/password"));
        assertThat(bindingResult.hasErrors(), is(false));
        assertThat(model.containsAttribute("successMessage"), is(true));
        ArgumentCaptor<ChangePasswordRequest> captor = ArgumentCaptor.forClass(ChangePasswordRequest.class);
        verify(useCase, times(1)).changePassword(captor.capture());
        assertThat(captor.getValue().getUserPublicId(), is("user-public-id"));
        assertThat(captor.getValue().getCurrentPassword(), is("current-password"));
        assertThat(captor.getValue().getNewPassword(), is("new-password"));
    }

    @Test
    public void changePasswordRejectsMismatchedConfirmation() {
        CredentialManagementUseCase useCase = mock(CredentialManagementUseCase.class);
        CredentialController controller = new CredentialController(useCase, new PasswordPolicy());
        ChangePasswordForm form = form("current-password", "new-password", "different-password");
        BindingResult bindingResult = bindingResult(form);

        controller.changePassword(form, bindingResult, new ExtendedModelMap());

        assertThat(bindingResult.hasFieldErrors("confirmPassword"), is(true));
        verify(useCase, org.mockito.Mockito.never()).changePassword(any(ChangePasswordRequest.class));
    }

    @Test
    public void changePasswordShowsErrorWhenCurrentPasswordIsInvalid() {
        CredentialManagementUseCase useCase = mock(CredentialManagementUseCase.class);
        doThrow(new InvalidCurrentPasswordException()).when(useCase)
                .changePassword(any(ChangePasswordRequest.class));
        CredentialController controller = new CredentialController(useCase, new PasswordPolicy());
        authenticateAs("user-public-id");
        BindingResult bindingResult = bindingResult(form("wrong-password", "new-password", "new-password"));

        controller.changePassword((ChangePasswordForm) bindingResult.getTarget(), bindingResult,
                new ExtendedModelMap());

        assertThat(bindingResult.hasFieldErrors("currentPassword"), is(true));
    }

    private CredentialController newController() {
        return new CredentialController(mock(CredentialManagementUseCase.class), new PasswordPolicy());
    }

    private ChangePasswordForm form(String current, String password, String confirmation) {
        ChangePasswordForm form = new ChangePasswordForm();
        form.setCurrentPassword(current);
        form.setNewPassword(password);
        form.setConfirmPassword(confirmation);
        return form;
    }

    private BindingResult bindingResult(ChangePasswordForm form) {
        return new BeanPropertyBindingResult(form, "changePasswordForm");
    }

    private void authenticateAs(String publicId) {
        Collection<GrantedAuthority> authorities = Arrays.<GrantedAuthority>asList(
                new GrantedAuthorityImpl("ROLE_USER"));
        IdentityUserDetails userDetails = new IdentityUserDetails(publicId, "user@example.com", "stored-hash",
                true, authorities);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
    }
}
