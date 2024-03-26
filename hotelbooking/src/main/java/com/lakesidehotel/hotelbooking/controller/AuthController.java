package com.lakesidehotel.hotelbooking.controller;

import com.lakesidehotel.hotelbooking.exception.UserAlreadyExistsException;
import com.lakesidehotel.hotelbooking.model.User;
import com.lakesidehotel.hotelbooking.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;


@RequiredArgsConstructor
public class AuthController {
    private final IUserService userService;

@PostMapping("/register-user")
    public ResponseEntity<?> registerUser(User user) {
        try {
            userService.registerUser(user);
            return ResponseEntity.ok("Registration successful");
        } catch (UserAlreadyExistsException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(exception.getMessage());
        }
    }
}
