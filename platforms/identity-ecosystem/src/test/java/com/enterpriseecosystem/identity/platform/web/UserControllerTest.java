package com.enterpriseecosystem.identity.platform.web;

import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import com.enterpriseecosystem.identity.identity.CreateUserUseCase;
import com.enterpriseecosystem.identity.identity.ChangeUserStateRequest;
import com.enterpriseecosystem.identity.identity.ChangeUserStateUseCase;
import com.enterpriseecosystem.identity.identity.InvalidUserStateChangeException;
import com.enterpriseecosystem.identity.identity.ListUsersUseCase;
import com.enterpriseecosystem.identity.credential.PasswordPolicy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class UserControllerTest {

    @Test
    public void listReturnsUsersView() {
        ListUsersUseCase listUsersUseCase = mock(ListUsersUseCase.class);
        UserController controller = new UserController(
                mock(CreateUserUseCase.class),
                listUsersUseCase,
                mock(ChangeUserStateUseCase.class),
                new PasswordPolicy());
        ExtendedModelMap model = new ExtendedModelMap();

        String viewName = controller.list(model);

        assertThat(viewName, is("users/index"));
        assertThat(model.containsAttribute("users"), is(true));
        verify(listUsersUseCase).listUsers();
    }

    @Test
    public void newUserReturnsFormView() {
        UserController controller = newController();
        ExtendedModelMap model = new ExtendedModelMap();

        String viewName = controller.newUser(model);

        assertThat(viewName, is("users/new"));
        assertThat(model.containsAttribute("createUserForm"), is(true));
    }

    @Test
    public void invalidPostReturnsFormViewWithErrors() {
        UserController controller = newController();
        CreateUserForm form = new CreateUserForm();
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "createUserForm");

        String viewName = controller.create(form, bindingResult, new ExtendedModelMap());

        assertThat(viewName, is("users/new"));
        assertThat(bindingResult.hasErrors(), is(true));
    }

    @Test
    public void shortPasswordReturnsFormViewWithPasswordError() {
        UserController controller = newController();
        CreateUserForm form = new CreateUserForm();
        form.setEmail("alice@example.com");
        form.setDisplayName("Alice");
        form.setPassword("12345678901");
        form.setConfirmPassword("12345678901");
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "createUserForm");

        String viewName = controller.create(form, bindingResult, new ExtendedModelMap());

        assertThat(viewName, is("users/new"));
        assertThat(bindingResult.hasFieldErrors("password"), is(true));
    }

    @Test
    public void passwordWithMinimumLengthPassesValidation() {
        CreateUserUseCase createUserUseCase = mock(CreateUserUseCase.class);
        UserController controller = new UserController(
                createUserUseCase,
                mock(ListUsersUseCase.class),
                mock(ChangeUserStateUseCase.class),
                new PasswordPolicy());
        CreateUserForm form = new CreateUserForm();
        form.setEmail("alice@example.com");
        form.setDisplayName("Alice");
        form.setPassword("123456789012");
        form.setConfirmPassword("123456789012");
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "createUserForm");

        String viewName = controller.create(form, bindingResult, new ExtendedModelMap());

        assertThat(viewName, is("users/new"));
        assertThat(bindingResult.hasErrors(), is(false));
        verify(createUserUseCase).create(org.mockito.Matchers.any(com.enterpriseecosystem.identity.identity.CreateUserRequest.class));
    }

    @Test
    public void changeStateRedirectsToUsersAfterSuccess() {
        ChangeUserStateUseCase changeUserStateUseCase = mock(ChangeUserStateUseCase.class);
        UserController controller = new UserController(
                mock(CreateUserUseCase.class),
                mock(ListUsersUseCase.class),
                changeUserStateUseCase,
                new PasswordPolicy());
        ChangeUserStateForm form = new ChangeUserStateForm();
        form.setPublicId("public-id-1");
        form.setStatus("LOCKED");

        String viewName = controller.changeState(form);

        assertThat(viewName, is("redirect:/users?stateChanged=true"));
        verify(changeUserStateUseCase).changeState(org.mockito.Matchers.any(ChangeUserStateRequest.class));
    }

    @Test
    public void changeStateRedirectsToUsersAfterFailure() {
        ChangeUserStateUseCase changeUserStateUseCase = mock(ChangeUserStateUseCase.class);
        doThrow(new InvalidUserStateChangeException())
                .when(changeUserStateUseCase)
                .changeState(org.mockito.Matchers.any(ChangeUserStateRequest.class));
        UserController controller = new UserController(
                mock(CreateUserUseCase.class),
                mock(ListUsersUseCase.class),
                changeUserStateUseCase,
                new PasswordPolicy());
        ChangeUserStateForm form = new ChangeUserStateForm();
        form.setPublicId("public-id-1");
        form.setStatus("DISABLED");

        String viewName = controller.changeState(form);

        assertThat(viewName, is("redirect:/users?stateChangeError=true"));
    }

    private UserController newController() {
        return new UserController(
                mock(CreateUserUseCase.class),
                mock(ListUsersUseCase.class),
                mock(ChangeUserStateUseCase.class),
                new PasswordPolicy());
    }
}
