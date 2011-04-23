package com.enterpriseecosystem.identity.platform.web;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.enterpriseecosystem.identity.identity.CreateUserRequest;
import com.enterpriseecosystem.identity.identity.CreateUserUseCase;
import com.enterpriseecosystem.identity.identity.ChangeUserStateRequest;
import com.enterpriseecosystem.identity.identity.ChangeUserStateUseCase;
import com.enterpriseecosystem.identity.identity.DuplicateEmailException;
import com.enterpriseecosystem.identity.identity.InvalidUserStateChangeException;
import com.enterpriseecosystem.identity.identity.ListUsersUseCase;
import com.enterpriseecosystem.identity.identity.UserNotFoundException;
import com.enterpriseecosystem.identity.credential.PasswordPolicy;

@Controller
public class UserController {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final CreateUserUseCase createUserUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final ChangeUserStateUseCase changeUserStateUseCase;
    private final PasswordPolicy passwordPolicy;

    @Autowired
    public UserController(CreateUserUseCase createUserUseCase,
                          ListUsersUseCase listUsersUseCase,
                          ChangeUserStateUseCase changeUserStateUseCase,
                          PasswordPolicy passwordPolicy) {
        this.createUserUseCase = createUserUseCase;
        this.listUsersUseCase = listUsersUseCase;
        this.changeUserStateUseCase = changeUserStateUseCase;
        this.passwordPolicy = passwordPolicy;
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("users", listUsersUseCase.listUsers());
        return "users/index";
    }

    @RequestMapping(value = "/users/new", method = RequestMethod.GET)
    public String newUser(Model model) {
        model.addAttribute("createUserForm", new CreateUserForm());
        return "users/new";
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public String create(@ModelAttribute("createUserForm") CreateUserForm form,
                         BindingResult bindingResult,
                         Model model) {
        validate(form, bindingResult);
        if (bindingResult.hasErrors()) {
            return "users/new";
        }

        try {
            createUserUseCase.create(new CreateUserRequest(form.getEmail(), form.getDisplayName(), form.getPassword()));
            model.addAttribute("createUserForm", new CreateUserForm());
            model.addAttribute("successMessage", "User created successfully.");
            return "users/new";
        } catch (DuplicateEmailException e) {
            bindingResult.rejectValue("email", "email.duplicate", "E-mail is already registered.");
            return "users/new";
        }
    }

    @RequestMapping(value = "/users/state", method = RequestMethod.POST)
    public String changeState(@ModelAttribute("changeUserStateForm") ChangeUserStateForm form) {
        try {
            changeUserStateUseCase.changeState(new ChangeUserStateRequest(form.getPublicId(), form.getStatus()));
            return "redirect:/users?stateChanged=true";
        } catch (InvalidUserStateChangeException e) {
            return "redirect:/users?stateChangeError=true";
        } catch (UserNotFoundException e) {
            return "redirect:/users?stateChangeError=true";
        }
    }

    private void validate(CreateUserForm form, BindingResult bindingResult) {
        if (isBlank(form.getEmail())) {
            bindingResult.rejectValue("email", "email.required", "E-mail is required.");
        } else if (!EMAIL_PATTERN.matcher(form.getEmail().trim()).matches()) {
            bindingResult.rejectValue("email", "email.invalid", "E-mail is invalid.");
        }

        if (isBlank(form.getDisplayName())) {
            bindingResult.rejectValue("displayName", "displayName.required", "Display name is required.");
        }

        if (isBlank(form.getPassword())) {
            bindingResult.rejectValue("password", "password.required", "Password is required.");
        } else if (!passwordPolicy.accepts(form.getPassword())) {
            bindingResult.rejectValue("password", "password.tooShort", "Password must have at least 12 characters.");
        }

        if (isBlank(form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "confirmPassword.required", "Password confirmation is required.");
        } else if (form.getPassword() != null && !form.getPassword().equals(form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "confirmPassword.mismatch", "Password confirmation does not match.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }
}
