package com.itk.jsonView.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.itk.jsonView.model.User;
import com.itk.jsonView.service.UserService;
import com.itk.jsonView.views.Views;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping
    @JsonView(Views.UserSummary.class)
    public List<User> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @JsonView(Views.UserDetails.class)
    public User getById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return service.save(user);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable UUID id, @Valid @RequestBody User user) {
        return service.update(id, user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}