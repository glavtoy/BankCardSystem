package ru.glavtoy.bankcardsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.glavtoy.bankcardsystem.dto.AuthRequest;
import ru.glavtoy.bankcardsystem.dto.AuthResponse;
import ru.glavtoy.bankcardsystem.dto.RegisterRequest;
import ru.glavtoy.bankcardsystem.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "Эндпоинты для входа и регистрации пользователей")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Вход пользователя")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok().build();
    }
}