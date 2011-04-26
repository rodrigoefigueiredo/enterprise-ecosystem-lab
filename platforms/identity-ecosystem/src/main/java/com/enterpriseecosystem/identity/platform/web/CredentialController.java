package com.enterpriseecosystem.identity.platform.web;

import com.enterpriseecosystem.identity.authentication.IdentityUserDetails;
import com.enterpriseecosystem.identity.credential.ChangePasswordRequest;
import com.enterpriseecosystem.identity.credential.CredentialManagementUseCase;
import com.enterpriseecosystem.identity.credential.InvalidCurrentPasswordException;
import com.enterpriseecosystem.identity.credential.InvalidPasswordException;
import com.enterpriseecosystem.identity.credential.PasswordPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class CredentialController {

    private final CredentialManagementUseCase credentialManagementUseCase;
    private final PasswordPolicy passwordPolicy;

    @Autowired
    public CredentialController(CredentialManagementUseCase credentialManagementUseCase,
                                PasswordPolicy passwordPolicy) {
        this.credentialManagementUseCase = credentialManagementUseCase;
        this.passwordPolicy = passwordPolicy;
    }

    @RequestMapping(value = "/me/password", method = RequestMethod.GET)
    public String changePassword(Model model) {
        model.addAttribute("changePasswordForm", new ChangePasswordForm());
        return "me/password";
    }

    @RequestMapping(value = "/me/password", method = RequestMethod.POST)
    public String changePassword(@ModelAttribute("changePasswordForm") ChangePasswordForm form,
                                 BindingResult bindingResult,
                                 Model model) {
        validate(form, bindingResult);
        if (bindingResult.hasErrors()) {
            return "me/password";
        }

        try {
            credentialManagementUseCase.changePassword(new ChangePasswordRequest(
                    currentUserPublicId(), form.getCurrentPassword(), form.getNewPassword()));
            model.addAttribute("changePasswordForm", new ChangePasswordForm());
            model.addAttribute("successMessage", "Password changed successfully.");
        } catch (InvalidCurrentPasswordException e) {
            bindingResult.rejectValue("currentPassword", "currentPassword.invalid",
                    "Current password is invalid.");
        } catch (InvalidPasswordException e) {
            bindingResult.rejectValue("newPassword", "newPassword.invalid",
                    "Password does not satisfy the password policy.");
        }

        return "me/password";
    }

    private void validate(ChangePasswordForm form, BindingResult bindingResult) {
        if (isBlank(form.getCurrentPassword())) {
            bindingResult.rejectValue("currentPassword", "currentPassword.required",
                    "Current password is required.");
        }
        if (isBlank(form.getNewPassword())) {
            bindingResult.rejectValue("newPassword", "newPassword.required",
                    "New password is required.");
        } else if (!passwordPolicy.accepts(form.getNewPassword())) {
            bindingResult.rejectValue("newPassword", "newPassword.invalid",
                    "Password must have at least 12 characters.");
        }
        if (isBlank(form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "confirmPassword.required",
                    "Password confirmation is required.");
        } else if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "confirmPassword.mismatch",
                    "Password confirmation does not match.");
        }
    }

    private String currentUserPublicId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof IdentityUserDetails)) {
            throw new IllegalStateException("Authenticated identity is unavailable.");
        }
        return ((IdentityUserDetails) authentication.getPrincipal()).getPublicId();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }
}
