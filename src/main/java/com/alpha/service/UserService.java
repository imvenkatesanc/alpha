package com.alpha.service;

import com.alpha.model.User;
import com.alpha.model.UserDto;

import java.util.List;

public interface UserService {

    // Saves a user
    User save(UserDto user);

    // Retrieves all users
    List<User> findAll();

    // Retrieves a user by username
    User findOne(String username);

    // Returns admin dashboard message
    User getAdminDashboard();

    // Retrieves current user's profile
    User getUserProfile();

    // Deletes a user by username
    String deleteUser(String username);

    User getUserById(Long id) throws Exception;

    // Updates current user's profile
    User updateUserProfile(UserDto user);

    User findByUsername(String username);
}