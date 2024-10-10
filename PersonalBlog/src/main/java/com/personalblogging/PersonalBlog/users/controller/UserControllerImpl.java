package com.personalblogging.PersonalBlog.users.controller;

import com.personalblogging.PersonalBlog.users.dto.UserDTO;
import com.personalblogging.PersonalBlog.users.model.User;
import com.personalblogging.PersonalBlog.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserControllerImpl implements UserController{
    private final UserService userService;
    @Override
    public ResponseEntity<?> registerUser(UserDTO userDTO) {
        return userService.registerUser(userDTO);
    }

    @Override
    public ResponseEntity<?> login(User user) {
        return userService.login(user);
    }

    @Override
    public ResponseEntity<?> getUserProfile(long id) {
        return userService.getUserProfile(id);
    }

    @Override
    public ResponseEntity<?> getUsers() {
        return userService.getUsers();
    }

    @Override
    public ResponseEntity<?> deleteUser(long id) {
        return userService.deleteUser(id);
    }

    @Override
    public ResponseEntity<?> getUserByUsername(String username) {
        return userService.getUserByUsername(username);
    }
}
