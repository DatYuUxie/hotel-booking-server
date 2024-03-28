package com.lakesidehotel.hotelbooking.controller;

import com.lakesidehotel.hotelbooking.exception.UserAlreadyExistsException;
import com.lakesidehotel.hotelbooking.model.User;
import com.lakesidehotel.hotelbooking.request.LoginRequest;
import com.lakesidehotel.hotelbooking.response.JwtResponse;
import com.lakesidehotel.hotelbooking.security.jwt.JwtUtils;
import com.lakesidehotel.hotelbooking.security.user.HotelUserDetails;
import com.lakesidehotel.hotelbooking.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final IUserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

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


    @PostMapping("/login")
    public ResponseEntity<?> authenticationUser(@Valid @RequestBody LoginRequest request) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtTokenForUser(authentication);
        HotelUserDetails userDetails = (HotelUserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority).toList();
        return ResponseEntity.ok(new JwtResponse(
                userDetails.getId(),
                userDetails.getEmail(),
                jwt,
                roles
        ));
    }
}
