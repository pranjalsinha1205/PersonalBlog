package com.personalblogging.PersonalBlog.blog.controller;

import com.personalblogging.PersonalBlog.blog.dto.BlogPostDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface BlogController {
    @PostMapping
    ResponseEntity<?> createBlog(@RequestBody BlogPostDTO blogPostDTO);

    @GetMapping("/{id}")
    ResponseEntity<?> getBlogById(@PathVariable Long id);

    @GetMapping
    ResponseEntity<?> getAllBlogs();

    @DeleteMapping("/delete/{id}")
    ResponseEntity<?> deleteBlog(@PathVariable Long id);

    @PutMapping("update/{id}")
    ResponseEntity<?> updateBlog(@PathVariable Long id, @RequestBody BlogPostDTO blogPostDTO);
}
