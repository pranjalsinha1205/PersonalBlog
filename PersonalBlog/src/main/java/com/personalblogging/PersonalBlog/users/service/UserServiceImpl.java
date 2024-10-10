package com.personalblogging.PersonalBlog.users.service;

import com.personalblogging.PersonalBlog.jwt.JwtHelper;
import com.personalblogging.PersonalBlog.jwt.JwtResponse;
import com.personalblogging.PersonalBlog.response.ResponseModel;
import com.personalblogging.PersonalBlog.response.UserResponse;
import com.personalblogging.PersonalBlog.role.model.Role;
import com.personalblogging.PersonalBlog.role.repository.RoleRepository;
import com.personalblogging.PersonalBlog.users.dto.UserDTO;
import com.personalblogging.PersonalBlog.users.model.User;
import com.personalblogging.PersonalBlog.users.repository.UserRepository;
import com.personalblogging.PersonalBlog.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtHelper jwtHelper;
    private final UserDetailsService userDetailsService;

    @Override
    @Transactional
    public ResponseEntity<?> registerUser(UserDTO userDTO) {
        ResponseModel responseModel = new ResponseModel();

        try{
            if (userDTO == null){
                responseModel.setMessage("User data is missing");
                return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
            }
            if (ObjectUtils.isEmpty(userDTO.getUsername()) || ObjectUtils.isEmpty(userDTO.getPassword())) {
                responseModel.setMessage("Required fields are missing");
                return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
            }

            if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
                responseModel.setMessage("Username already exists");
                return new ResponseEntity<>(responseModel, HttpStatus.CONFLICT);
            }

            if (userRepository.findByUsernameIgnoreCase(userDTO.getUsername()).isPresent()) {
                responseModel.setMessage("Username already exists");
                return new ResponseEntity<>(responseModel, HttpStatus.CONFLICT);
            }

            User user = new User();
            user.setUsername(userDTO.getUsername());
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            user.setCreatedAt(userDTO.getCreatedAt());

            Optional<Role> role = roleRepository.findById(userDTO.getRole());
            if(role.isPresent()){
                user.setRole(role.get());
            }
            else {
                responseModel.setMessage("Role not found");
                return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
            }

            User savedUser = userRepository.save(user);

            //user response
            UserResponse responseDTO = new UserResponse();
            responseDTO.setId(savedUser.getId());
            responseDTO.setUsername(savedUser.getUsername());
            responseDTO.setRole(savedUser.getRole().getRole().name());
            responseDTO.setCreatedAt(savedUser.getCreatedAt());

            responseModel.setMessage("User created successfully");
            responseModel.setData(responseDTO);

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);

        }catch (IllegalArgumentException e) {
            responseModel.setMessage("Invalid user type or role type");
            return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            responseModel.setMessage("An error occured: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @Override
    public ResponseEntity<?> login(User user) {
        ResponseModel responseModel = new ResponseModel();
        try{
            if (!user.getUsername().matches(Constants.usernameRegex)){
                responseModel.setMessage("Invalid username format! Please limit the number of entities in the username between 3 to 20");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseModel);
            }

            if(!user.getPassword().matches(Constants.passwordRegex)){
                responseModel.setMessage("Invalid password format! Please limit the number of entities in the password between 8 and 20");
            }

            Optional<User> userOptional = userRepository.findByUsername(user.getUsername());
            if(!userOptional.isPresent()){
                responseModel.setMessage("Username not found!");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseModel);
            }

            User user1 = userOptional.get();
            if (!passwordEncoder.matches(user.getPassword(), user1.getPassword())) {
                responseModel.setMessage("Invalid password");
                return new ResponseEntity<>(responseModel, HttpStatus.UNAUTHORIZED);
            }

            Role role = user1.getRole();
            String jwtToken = jwtHelper.generateToken(userDetailsService.loadUserByUsername(user1.getUsername()), user1, role);
            JwtResponse jwtResponse = JwtResponse.builder().jwtToken(jwtToken).build();
            responseModel.setMessage("Login successful");
            responseModel.setData(jwtResponse);

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e){
            responseModel.setMessage("An error occured: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @Override
    public ResponseEntity<?> getUserProfile(long id) {
        ResponseModel responseModel = new ResponseModel();

        Optional<User> user = userRepository.findById(id);
        if(!user.isPresent()){
            responseModel.setMessage("No user with id " + id);
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseModel);
        }

        //if present
        User user1 = user.get();

        UserResponse responseDTO = new UserResponse();
        responseDTO.setId(user1.getId());
        responseDTO.setUsername(user1.getUsername());
        responseDTO.setRole(user1.getRole().getRole().name());
        responseDTO.setCreatedAt(user1.getCreatedAt());

        responseModel.setData(responseDTO);
        responseModel.setMessage("User fetched successfully");

        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @Override
    public ResponseEntity<?> deleteUser(long id) {
        ResponseModel responseModel = new ResponseModel();

        Optional<User> user = userRepository.findById(id);
        if(!user.isPresent()){
            responseModel.setMessage("No user with id " + id + " found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseModel);
        }

        userRepository.deleteById(id);
        responseModel.setMessage("User with id " + id + " has been successfully deleted");
        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @Override
    public ResponseEntity<?> getUsers() {
        ResponseModel responseModel = new ResponseModel();

        List<User> userList = userRepository.findAll();
        if (userList.size() == 0){
            responseModel.setMessage("No users in the database");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseModel);
        }
        List<UserResponse> userResponses = userList.stream()
                .map(users -> new UserResponse(
                        users.getId(),
                        users.getUsername(),
                        users.getRole().getRole().name(),
                        users.getCreatedAt()
                ))
                .collect(Collectors.toList());
        responseModel.setMessage("Users list retrieved");
        responseModel.setData(userResponses);
        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @Override
    public ResponseEntity<?> getUserByUsername(String username) {
        ResponseModel responseModel = new ResponseModel();

        Optional<Object> userOptional = userRepository.findByUsernameIgnoreCase(username);
        if (!userOptional.isPresent()){
            responseModel.setMessage("No user with this username found!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseModel);
        }

        User user = (User) userOptional.get();

        UserResponse responseDTO = new UserResponse();
        responseDTO.setId(user.getId());
        responseDTO.setUsername(user.getUsername());
        responseDTO.setRole(user.getRole().getRole().name());
        responseDTO.setCreatedAt(user.getCreatedAt());

        responseModel.setMessage("User retrieved successfully");
        responseModel.setData(responseDTO);

        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }
}
