package com.enterpriseecosystem.identity.platform.web;

import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import com.enterpriseecosystem.identity.identity.CreateUserUseCase;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class UserControllerTest {

    @Test
    public void newUserReturnsFormView() {
        UserController controller = new UserController(mock(CreateUserUseCase.class));
        ExtendedModelMap model = new ExtendedModelMap();

        String viewName = controller.newUser(model);

        assertThat(viewName, is("users/new"));
        assertThat(model.containsAttribute("createUserForm"), is(true));
    }

    @Test
    public void invalidPostReturnsFormViewWithErrors() {
        UserController controller = new UserController(mock(CreateUserUseCase.class));
        CreateUserForm form = new CreateUserForm();
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "createUserForm");

        String viewName = controller.create(form, bindingResult, new ExtendedModelMap());

        assertThat(viewName, is("users/new"));
        assertThat(bindingResult.hasErrors(), is(true));
    }
}
