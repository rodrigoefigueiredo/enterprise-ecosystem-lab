package com.enterpriseecosystem.identity.platform.web;

import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import com.enterpriseecosystem.identity.identity.CreateUserUseCase;
import com.enterpriseecosystem.identity.identity.ListUsersUseCase;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class UserControllerTest {

    @Test
    public void listReturnsUsersView() {
        ListUsersUseCase listUsersUseCase = mock(ListUsersUseCase.class);
        UserController controller = new UserController(mock(CreateUserUseCase.class), listUsersUseCase);
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

    private UserController newController() {
        return new UserController(mock(CreateUserUseCase.class), mock(ListUsersUseCase.class));
    }
}
