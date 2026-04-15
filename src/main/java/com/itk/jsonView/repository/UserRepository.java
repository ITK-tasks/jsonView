package com.itk.jsonView.repository;

import com.itk.jsonView.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(@Email(message = "Invalid email")
                          @NotBlank(message = "Email is required") String email);
}
