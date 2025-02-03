package com.piterjk.springbootdemo.users.service;

import com.piterjk.springbootdemo.users.entity.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
    User saveUser(User user);
}
