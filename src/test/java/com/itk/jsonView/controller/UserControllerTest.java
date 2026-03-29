package com.itk.jsonView.controller;

import com.itk.jsonView.exception.GlobalExceptionHandler;
import com.itk.jsonView.model.Order;
import com.itk.jsonView.model.User;
import com.itk.jsonView.service.UserService;
import org.junit.jupiter.api.Test;
import static org.mockito.BDDMockito.given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
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
        user.setName("John");
        user.setEmail("john@mail.com");

        Order order = new Order();
        order.setId(UUID.randomUUID());
        user.setOrders(List.of(order));
        given(userService.findAll()).willReturn(List.of(user));
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[0].email").value("john@mail.com"))
                .andExpect(jsonPath("$[0].orders").doesNotExist());
    }

}
