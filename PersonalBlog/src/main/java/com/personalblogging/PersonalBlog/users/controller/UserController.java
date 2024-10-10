package com.personalblogging.PersonalBlog.users.controller;

import com.personalblogging.PersonalBlog.users.dto.UserDTO;
import com.personalblogging.PersonalBlog.users.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserController {
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO);

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user);

    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable long id);
}
