package com.personalblogging.PersonalBlog.blog.service;

import com.personalblogging.PersonalBlog.blog.dto.BlogPostDTO;
import com.personalblogging.PersonalBlog.blog.model.BlogPost;
import com.personalblogging.PersonalBlog.blog.repository.BlogPostRepository;
import com.personalblogging.PersonalBlog.response.BlogResponse;
import com.personalblogging.PersonalBlog.response.ResponseModel;
import com.personalblogging.PersonalBlog.users.model.User;
import com.personalblogging.PersonalBlog.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogPostServiceImpl implements BlogPostService{

    private final BlogPostRepository blogPostRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ResponseEntity<?> createBlog(BlogPostDTO blogPostDTO) {
        ResponseModel responseModel = new ResponseModel();

        try {
            //checking the missing values
            if (!StringUtils.hasText(blogPostDTO.getTitle())) {
                responseModel.setMessage("Title is missing");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseModel);
            }
            if (!StringUtils.hasText(blogPostDTO.getContent())) {
                responseModel.setMessage("Content is missing");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseModel);
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = null;

            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();
            }

            // Fetch the User object from the repository
            Optional<User> optionalAuthor = userRepository.findByUsername(currentUsername);
            if (optionalAuthor.isEmpty()) {
                responseModel.setMessage("Author not found");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseModel);
            }

            User author = optionalAuthor.get();
            BlogPost blogPost = new BlogPost();
            blogPost.setTitle(blogPostDTO.getTitle());
            blogPost.setContent(blogPostDTO.getContent());
            blogPost.setAuthor(author);
            blogPost.setCreatedAt(blogPostDTO.getCreatedAt());
            blogPost.setUpdatedAt(blogPostDTO.getUpdatedAt());

            //saving the data
            BlogPost savedBlog = blogPostRepository.save(blogPost);

            //tailoring the response
            BlogResponse responseDTO = new BlogResponse();
            responseDTO.setId(savedBlog.getId());
            responseDTO.setTitle(savedBlog.getTitle());
            responseDTO.setAuthor(savedBlog.getAuthor().getUsername());
            responseDTO.setContent(savedBlog.getContent());
            responseDTO.setCreatedAt(savedBlog.getCreatedAt());
            responseDTO.setUpdatedAt(savedBlog.getUpdatedAt());

            responseModel.setMessage("Blog post created successfully");
            responseModel.setData(responseDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseModel);
        }catch (Exception e){
            responseModel.setMessage("An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @Override
    public ResponseEntity<?> getBlogById(Long id) {
        ResponseModel responseModel = new ResponseModel();

        Optional<BlogPost> blogPostOptional = blogPostRepository.findById(id);
        if(!blogPostOptional.isPresent()){
            responseModel.setMessage("Blog not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseModel);
        }

        BlogResponse responseDTO = new BlogResponse();
        responseDTO.setId(blogPostOptional.get().getId());
        responseDTO.setTitle(blogPostOptional.get().getTitle());
        responseDTO.setAuthor(blogPostOptional.get().getAuthor().getUsername());
        responseDTO.setContent(blogPostOptional.get().getContent());
        responseDTO.setCreatedAt(blogPostOptional.get().getCreatedAt());
        responseDTO.setUpdatedAt(blogPostOptional.get().getUpdatedAt());

        responseModel.setMessage("Blog retrieved successfully");
        responseModel.setData(responseDTO);

        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @Override
    public ResponseEntity<?> getAllBlogs() {
        ResponseModel responseModel = new ResponseModel();

        List<BlogPost> blogPostList = blogPostRepository.findAll();

        List<BlogResponse> responseList = blogPostList.stream()
                .map(blogPost -> new BlogResponse(
                        blogPost.getId(),
                        blogPost.getTitle(),
                        blogPost.getContent(),
                        blogPost.getAuthor().getUsername(),
                        blogPost.getCreatedAt(),
                        blogPost.getUpdatedAt()
                ))
                .collect(Collectors.toList());

        responseModel.setData(responseList);
        responseModel.setMessage("All blogs retrieved successfully");

        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteBlog(Long id) {
        ResponseModel responseModel = new ResponseModel();

        Optional<BlogPost> blogPost = blogPostRepository.findById(id);
        if(!blogPost.isPresent()){
            responseModel.setMessage("No blog with the id " + id + " found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseModel);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = null;

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();
        }

        if (!currentUsername.equals(blogPost.get().getAuthor().getUsername())){
            responseModel.setMessage("You can only delete your own posts!");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseModel);
        }

        blogPostRepository.deleteById(id);
        responseModel.setMessage("Blog deleted successfully");
        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @Override
    @Transactional
    public ResponseEntity<?> updateBlog(Long id, BlogPostDTO blogPostDTO) {
        ResponseModel responseModel = new ResponseModel();

        try {
            Optional<BlogPost> blogPost = blogPostRepository.findById(id);
            if (!blogPost.isPresent()) {
                responseModel.setMessage("No blog with the id " + id + " found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseModel);
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = null;

            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();
            }

            if (!currentUsername.equals(blogPost.get().getAuthor().getUsername())){
                responseModel.setMessage("You can only update your own posts!");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseModel);
            }

            BlogPost blogPost1 = blogPost.get();

            if (blogPostDTO.getContent() != null) {
                blogPost1.setContent(blogPostDTO.getContent());
            }

            blogPost1.setUpdatedAt(LocalDateTime.now());

            BlogPost savedBlog = blogPostRepository.save(blogPost1);

            BlogResponse responseDTO = new BlogResponse();
            responseDTO.setId(savedBlog.getId());
            responseDTO.setTitle(savedBlog.getTitle());
            responseDTO.setAuthor(savedBlog.getAuthor().getUsername());
            responseDTO.setContent(savedBlog.getContent());
            responseDTO.setCreatedAt(savedBlog.getCreatedAt());
            responseDTO.setUpdatedAt(savedBlog.getUpdatedAt());

            responseModel.setData(responseDTO);
            responseModel.setMessage("Blog updated successfully");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e){
            responseModel.setMessage("An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }
}
