package com.itk.jsonView.service;

import com.itk.jsonView.exception.BusinessException;
import com.itk.jsonView.exception.DuplicateResourceException;
import com.itk.jsonView.exception.EntityNotFoundException;
import com.itk.jsonView.model.User;
import com.itk.jsonView.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));
    }

    @Transactional
    public User save(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("User", "email", user.getEmail());
        }
        user.setId(UUID.randomUUID());
        try {
            return userRepository.save(user);
        } catch (DataAccessException e) {
            log.error("Failed to save user: {}", e.getMessage());
            throw new BusinessException("Failed to save user", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Transactional
    public User update(UUID id, User user) {
        User existing = findById(id);
        if (!existing.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("User", "email", user.getEmail());
        }
        existing.setName(user.getName());
        existing.setEmail(user.getEmail());
        return userRepository.save(existing);
    }

    @Transactional
    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User", id);
        }
        try {
            userRepository.deleteById(id);
        } catch (DataAccessException e) {
            throw new BusinessException(
                    "Cannot delete user with existing orders",
                    HttpStatus.CONFLICT
            );
        }
    }
}