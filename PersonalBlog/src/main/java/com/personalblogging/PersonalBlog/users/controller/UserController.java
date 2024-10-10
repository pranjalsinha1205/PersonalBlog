package com.personalblogging.PersonalBlog.users.controller;

import com.personalblogging.PersonalBlog.users.dto.UserDTO;
import com.personalblogging.PersonalBlog.users.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

public interface UserController {
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO);

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user);

    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable long id);

    @GetMapping("/profile")
    public ResponseEntity<?> getUsers();

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id);
}
