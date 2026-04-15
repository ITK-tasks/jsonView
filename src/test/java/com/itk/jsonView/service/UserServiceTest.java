package com.itk.jsonView.service;

import com.itk.jsonView.exception.DuplicateResourceException;
import com.itk.jsonView.exception.EntityNotFoundException;
import com.itk.jsonView.model.User;
import com.itk.jsonView.repository.UserRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
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

    private User testUser;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setName("John");
        testUser.setEmail("john@mail.com");
    }

    @Test
    void shouldReturnAllUsers() {
        List<User> users = List.of(testUser, new User());
        given(userRepository.findAll()).willReturn(users);
        List<User> result = userService.findAll();
        assertThat(result).hasSize(2);
        then(userRepository).should().findAll();
    }

    @Test
    void shouldReturnUserById() {
        given(userRepository.findById(testUserId)).willReturn(Optional.of(testUser));
        User result = userService.findById(testUserId);
        assertThat(result.getId()).isEqualTo(testUserId);
        then(userRepository).should().findById(testUserId);
    }

    @Test
    void shouldThrowIfUserNotFound() {
        given(userRepository.findById(testUserId)).willReturn(Optional.empty());
        assertThatThrownBy(() -> userService.findById(testUserId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User with id " + testUserId + " not found");
    }

    @Test
    void shouldSaveUser() {
        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmail("new@mail.com");
        given(userRepository.existsByEmail(newUser.getEmail())).willReturn(false);
        given(userRepository.save(any(User.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        User result = userService.save(newUser);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("New User");
        assertThat(result.getEmail()).isEqualTo("new@mail.com");
        then(userRepository).should().save(any(User.class));
    }

    @Test
    void shouldThrowWhenSavingDuplicateEmail() {
        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmail("existing@mail.com");
        given(userRepository.existsByEmail(newUser.getEmail())).willReturn(true);
        assertThatThrownBy(() -> userService.save(newUser))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("User with email : existing@mail.com already exists");
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    void shouldUpdateUser() {
        User updatedUser = new User();
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("updated@mail.com");
        given(userRepository.findById(testUserId)).willReturn(Optional.of(testUser));
        given(userRepository.existsByEmail(updatedUser.getEmail())).willReturn(false);
        given(userRepository.save(any(User.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        User result = userService.update(testUserId, updatedUser);
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getEmail()).isEqualTo("updated@mail.com");
        assertThat(result.getId()).isEqualTo(testUserId);
        then(userRepository).should().save(testUser);
    }

    @Test
    void shouldUpdateUserWhenEmailNotChanged() {
        User updatedUser = new User();
        updatedUser.setName("Updated Name");
        updatedUser.setEmail(testUser.getEmail()); // Same email
        given(userRepository.findById(testUserId)).willReturn(Optional.of(testUser));
        given(userRepository.save(any(User.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        User result = userService.update(testUserId, updatedUser);
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getEmail()).isEqualTo(testUser.getEmail());
        then(userRepository).should(never()).existsByEmail(any());
    }

    @Test
    void shouldThrowWhenUpdatingNonExistentUser() {
        UUID nonExistentId = UUID.randomUUID();
        User updatedUser = new User();
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("updated@mail.com");
        given(userRepository.findById(nonExistentId)).willReturn(Optional.empty());
        assertThatThrownBy(() -> userService.update(nonExistentId, updatedUser))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User with id " + nonExistentId + " not found");
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    void shouldThrowWhenUpdatingWithDuplicateEmail() {
        User updatedUser = new User();
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("duplicate@mail.com");
        given(userRepository.findById(testUserId)).willReturn(Optional.of(testUser));
        given(userRepository.existsByEmail(updatedUser.getEmail())).willReturn(true);
        assertThatThrownBy(() -> userService.update(testUserId, updatedUser))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("User with email : duplicate@mail.com already exists");
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    void shouldDeleteUser() {
        given(userRepository.existsById(testUserId)).willReturn(true);
        userService.delete(testUserId);
        then(userRepository).should().deleteById(testUserId);
    }

    @Test
    void shouldThrowWhenDeletingNonExistentUser() {
        given(userRepository.existsById(testUserId)).willReturn(false);
        assertThatThrownBy(() -> userService.delete(testUserId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User with id " + testUserId + " not found");
        then(userRepository).should(never()).deleteById(any());
    }
}