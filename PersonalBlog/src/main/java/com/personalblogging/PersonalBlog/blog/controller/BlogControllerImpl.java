package com.personalblogging.PersonalBlog.blog.controller;

import com.personalblogging.PersonalBlog.blog.dto.BlogPostDTO;
import com.personalblogging.PersonalBlog.blog.service.BlogPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/blog")
@RequiredArgsConstructor
public class BlogControllerImpl implements BlogController{

    private final BlogPostService blogPostService;

    @Override
    public ResponseEntity<?> createBlog(BlogPostDTO blogPostDTO) {
        return blogPostService.createBlog(blogPostDTO);
    }

    @Override
    public ResponseEntity<?> getBlogById(Long id) {
        return blogPostService.getBlogById(id);
    }

    @Override
    public ResponseEntity<?> getAllBlogs() {
        return blogPostService.getAllBlogs();
    }

    @Override
    public ResponseEntity<?> deleteBlog(Long id) {
        return blogPostService.deleteBlog(id);
    }

    @Override
    public ResponseEntity<?> updateBlog(Long id, BlogPostDTO blogPostDTO) {
        return blogPostService.updateBlog(id, blogPostDTO);
    }
}
