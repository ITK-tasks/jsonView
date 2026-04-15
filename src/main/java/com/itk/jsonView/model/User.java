package com.itk.jsonView.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.itk.jsonView.views.Views;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @JsonView(Views.UserSummary.class)
    private UUID id;

    @JsonView(Views.UserSummary.class)
    private String name;

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    @JsonView(Views.UserSummary.class)
    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonView(Views.UserDetails.class)
    private List<Order> orders;
}