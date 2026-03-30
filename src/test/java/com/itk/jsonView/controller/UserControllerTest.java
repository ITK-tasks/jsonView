package com.itk.jsonView.controller;

import com.itk.jsonView.exception.EntityNotFoundException;
import com.itk.jsonView.exception.GlobalExceptionHandler;
import com.itk.jsonView.model.Order;
import com.itk.jsonView.model.User;
import com.itk.jsonView.service.UserService;
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

    @Test
    void shouldReturnUserSummaryView() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Alex");
        user.setEmail("alex@mail.com");

        Order order = new Order();
        order.setId(UUID.randomUUID());
        user.setOrders(List.of(order));
        given(userService.findAll()).willReturn(List.of(user));
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").value("Alex"))
                .andExpect(jsonPath("$[0].email").value("alex@mail.com"))
                .andExpect(jsonPath("$[0].orders").doesNotExist());
    }

    @Test
    void shouldReturnUserDetailsView() throws Exception {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setName("Alex");
        user.setEmail("alex@mail.com");
        Order order = new Order();
        order.setId(UUID.randomUUID());
        user.setOrders(List.of(order));
        given(userService.findById(id)).willReturn(user);
        mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orders").exists())
                .andExpect(jsonPath("$.orders[0].id").exists());
    }

    @Test
    void shouldCreateUser() throws Exception {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@mail.com");
        given(userService.save(any(User.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alex"));
    }

    @Test
    void shouldFailValidation() throws Exception {
        User user = new User();
        user.setName("Alex");
        user.setEmail("");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
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
    void shouldDeleteUser() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(delete("/users/{id}", id))
                .andExpect(status().isOk());
        then(userService).should().delete(id);
    }

}
