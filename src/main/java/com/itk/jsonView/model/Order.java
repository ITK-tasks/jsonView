package com.itk.jsonView.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.itk.jsonView.views.Views;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Order {

    @Id
    @JsonView(Views.UserDetails.class)
    private UUID id;

    @JsonView(Views.UserDetails.class)
    private String products;

    @JsonView(Views.UserDetails.class)
    private BigDecimal amount;

    @JsonView(Views.UserDetails.class)
    private LocalDateTime createdAt;

    @JsonView(Views.UserDetails.class)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}