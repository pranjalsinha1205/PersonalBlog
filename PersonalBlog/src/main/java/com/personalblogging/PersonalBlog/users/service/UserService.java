package com.personalblogging.PersonalBlog.users.service;

import com.personalblogging.PersonalBlog.users.dto.UserDTO;
import com.personalblogging.PersonalBlog.users.model.User;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<?> getUserProfile(long id);

    ResponseEntity<?> login(User user);

    ResponseEntity<?> registerUser(UserDTO userDTO);

    ResponseEntity<?> deleteUser(long id);

    ResponseEntity<?> getUsers();

    ResponseEntity<?> getUserByUsername(String username);

    ResponseEntity<?> registerAdmin(UserDTO userDTO);
}
