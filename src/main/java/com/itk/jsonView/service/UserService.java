package com.itk.jsonView.service;

import com.itk.jsonView.model.User;
import com.itk.jsonView.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public User save(User user) {
        user.setId(UUID.randomUUID());
        return userRepository.save(user);
    }

    @Transactional
    public User update(UUID id, User user) {
        User existing = findById(id);
        existing.setName(user.getName());
        existing.setEmail(user.getEmail());
        return userRepository.save(existing);
    }

    @Transactional
    public void delete(UUID id) {
        userRepository.deleteById(id);
    }
}