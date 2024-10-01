package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;


import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {
    private UserService userService;
    private UserController userController;

    @BeforeEach
    public void initServices() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    public void whenRequestGetPageRegistration() {
        var model = new ConcurrentModel();
        var view = userController.getRegistrationPage(model);
        assertThat(view).isEqualTo("users/register");
    }

    @Test
    public void whenPostUserRegistrationAndGetPageRegistration() {
        var user = new User(1, "test@mail.ru", "test", "12345");
        when(userService.save(any(User.class))).thenReturn(Optional.of(user));
        var model = new ConcurrentModel();
        var view = userController.register(user, model);
        assertThat(view).isEqualTo("redirect:/");

    }

    @Test
    public void whenRequestLoginPage() {
        var model = new ConcurrentModel();
        var view = userController.getLoginPage(model);
        assertThat(view).isEqualTo("users/login");
    }

    @Test
    public void whenPostLoginUser() {
        var user = new User(1, "name@mail.ru", "name", "password");
        when(userService.findByEmailAndPassword(any(String.class), any(String.class))).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.loginUser(user, model, new MockHttpServletRequest());
        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void whenLogoutUser() {
        var user = new User(1, "name@mail.ru", "name", "password");
        when(userService.findByEmailAndPassword(any(String.class), any(String.class))).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        userController.loginUser(user, model, new MockHttpServletRequest());
        var view = userController.logout(new MockHttpSession());
        assertThat(view).isEqualTo("redirect:/users/login");
    }

}