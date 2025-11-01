package com.pthttt.authen.service;

import com.pthttt.authen.model.User;
import com.pthttt.authen.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User createUser(User user) {

        User userFindById = userRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("User not found"));

        if(userFindById != null) {
            throw new RuntimeException("User already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(new Date());
        user.setRole("USER");
        return userRepository.save(user);
    }
}