package com.personalblogging.PersonalBlog.blog.repository;

import com.personalblogging.PersonalBlog.blog.model.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
}
