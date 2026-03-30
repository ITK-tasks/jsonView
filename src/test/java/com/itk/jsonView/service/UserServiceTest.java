package com.itk.jsonView.service;

import com.itk.jsonView.model.User;
import com.itk.jsonView.repository.UserRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnAllUsers() {
        List<User> users = List.of(new User(), new User());
        given(userRepository.findAll()).willReturn(users);
        List<User> result = userService.findAll();
        assertThat(result).hasSize(2);
        then(userRepository).should().findAll();
    }

    @Test
    void shouldReturnUserById() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        given(userRepository.findById(id)).willReturn(Optional.of(user));
        User result = userService.findById(id);
        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    void shouldThrowIfUserNotFound() {
        UUID id = UUID.randomUUID();
        given(userRepository.findById(id)).willReturn(Optional.empty());
        assertThatThrownBy(() -> userService.findById(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }

    @Test
    void shouldSaveUser() {
        User user = new User();
        user.setName("John");
        given(userRepository.save(any(User.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        User result = userService.save(user);
        assertThat(result.getId()).isNotNull();
        then(userRepository).should().save(user);
    }

    @Test
    void shouldUpdateUser() {
        UUID id = UUID.randomUUID();
        User existing = new User();
        existing.setId(id);
        User updated = new User();
        updated.setName("New");
        updated.setEmail("new@mail.com");
        given(userRepository.findById(id)).willReturn(Optional.of(existing));
        given(userRepository.save(any(User.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        User result = userService.update(id, updated);
        assertThat(result.getName()).isEqualTo("New");
        assertThat(result.getEmail()).isEqualTo("new@mail.com");
    }

    @Test
    void shouldDeleteUser() {
        UUID id = UUID.randomUUID();
        userService.delete(id);
        then(userRepository).should().deleteById(id);
    }


}