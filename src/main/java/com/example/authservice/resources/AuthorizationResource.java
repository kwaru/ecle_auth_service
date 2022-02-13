package com.example.authservice.resources;

import com.example.authservice.entities.User;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.wrappers.ResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

/**
 * @author momondi
 */
@RestController
public class AuthorizationResource {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthorizationResource(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //creates record when application runs
    @PostConstruct
    public void init() {
        try {
            User username = userRepository.findByUsername("admin");
            if (null == username) {
                User user = new User();
                user.setUsername("admin");
                user.setPassword(passwordEncoder.encode("admin"));
                userRepository.save(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/create-user")
    public ResponseEntity createUser(@RequestBody User user) {
        ResponseWrapper response = new ResponseWrapper();
        User username = userRepository.findByUsername(user.getUsername());
        if (null != username) {
            response.setCode(409);
            response.setMessage("username already exists");
            return new ResponseEntity(response, HttpStatus.CONFLICT);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        response.setCode(201);
        response.setData(user);
        return new ResponseEntity(response, HttpStatus.CREATED);
    }
}
