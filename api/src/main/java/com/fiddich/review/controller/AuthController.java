package com.fiddich.review.controller;

import com.fiddich.review.auth.AuthService;
import com.fiddich.review.auth.TokenResponse;
import com.fiddich.review.common.response.ApiResponse;
import com.fiddich.review.dto.request.LoginRequest;
import com.fiddich.review.dto.request.RegisterRequest;
import com.fiddich.review.user.AuthProvider;
import com.fiddich.review.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        String encodedPassword = authService.encodePassword(request.password());
        userService.register(request.email(), encodedPassword, request.name(), request.platform(), AuthProvider.EMAIL);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse tokens = authService.login(request.email(), request.password());
        return ResponseEntity.ok(ApiResponse.ok(tokens));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@RequestHeader("Refresh-Token") String refreshToken) {
        TokenResponse tokens = authService.refresh(refreshToken);
        return ResponseEntity.ok(ApiResponse.ok(tokens));
    }
}
