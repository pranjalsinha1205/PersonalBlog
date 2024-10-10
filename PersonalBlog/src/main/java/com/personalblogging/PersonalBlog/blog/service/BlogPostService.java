package com.personalblogging.PersonalBlog.blog.service;

import com.personalblogging.PersonalBlog.blog.dto.BlogPostDTO;
import org.springframework.http.ResponseEntity;

public interface BlogPostService {
    ResponseEntity<?> createBlog(BlogPostDTO blogPostDTO);

    ResponseEntity<?> getBlogById(Long id);

    ResponseEntity<?> getAllBlogs();

    ResponseEntity<?> deleteBlog(Long id);

    ResponseEntity<?> updateBlog(Long id, BlogPostDTO blogPostDTO);
}
