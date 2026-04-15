package com.itk.jsonView.controller;

import com.itk.jsonView.exception.DuplicateResourceException;
import com.itk.jsonView.exception.EntityNotFoundException;
import com.itk.jsonView.exception.GlobalExceptionHandler;
import com.itk.jsonView.model.Order;
import com.itk.jsonView.model.User;
import com.itk.jsonView.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import tools.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;
import java.util.UUID;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private User testUser;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setName("Alex");
        testUser.setEmail("alex@mail.com");

        Order order = new Order();
        order.setId(UUID.randomUUID());
        testUser.setOrders(List.of(order));
    }

    @Test
    void shouldReturnUserSummaryView() throws Exception {
        given(userService.findAll()).willReturn(List.of(testUser));
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").value("Alex"))
                .andExpect(jsonPath("$[0].email").value("alex@mail.com"))
                .andExpect(jsonPath("$[0].orders").doesNotExist());
    }

    @Test
    void shouldReturnEmptyListWhenNoUsers() throws Exception {
        given(userService.findAll()).willReturn(List.of());
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldReturnUserDetailsView() throws Exception {
        given(userService.findById(testUserId)).willReturn(testUser);
        mockMvc.perform(get("/users/{id}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUserId.toString()))
                .andExpect(jsonPath("$.name").value("Alex"))
                .andExpect(jsonPath("$.email").value("alex@mail.com"))
                .andExpect(jsonPath("$.orders").exists())
                .andExpect(jsonPath("$.orders[0].id").exists());
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        given(userService.findById(testUserId))
                .willThrow(new EntityNotFoundException("User", testUserId));
        mockMvc.perform(get("/users/{id}", testUserId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("User with id " + testUserId + " not found"));
    }

    @Test
    void shouldCreateUser() throws Exception {
        User newUser = new User();
        newUser.setName("Bob");
        newUser.setEmail("bob@mail.com");
        given(userService.save(any(User.class)))
                .willAnswer(invocation -> {
                    User saved = invocation.getArgument(0);
                    saved.setId(UUID.randomUUID());
                    return saved;
                });
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bob"))
                .andExpect(jsonPath("$.email").value("bob@mail.com"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void shouldReturnConflictWhenCreatingDuplicateUser() throws Exception {
        User newUser = new User();
        newUser.setName("Bob");
        newUser.setEmail("existing@mail.com");
        given(userService.save(any(User.class)))
                .willThrow(new DuplicateResourceException("User", "email", "existing@mail.com"));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("BUSINESS_ERROR"))
                .andExpect(jsonPath("$.message").value("User with email : existing@mail.com already exists"));
    }


    @Test
    void shouldUpdateUser() throws Exception {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setName("Updated");
        user.setEmail("updated@mail.com");
        given(userService.update(eq(id), any(User.class)))
                .willAnswer(invocation -> invocation.getArgument(1));
        mockMvc.perform(put("/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentUser() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        User updatedUser = new User();
        updatedUser.setName("Updated");
        updatedUser.setEmail("updated@mail.com");
        given(userService.update(eq(nonExistentId), any(User.class)))
                .willThrow(new EntityNotFoundException("User", nonExistentId));
        mockMvc.perform(put("/users/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(delete("/users/{id}", id))
                .andExpect(status().isOk());
        then(userService).should().delete(id);
    }

}
