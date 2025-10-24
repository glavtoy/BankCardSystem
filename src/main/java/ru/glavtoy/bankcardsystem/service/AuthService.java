package ru.glavtoy.bankcardsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.glavtoy.bankcardsystem.dto.AuthRequest;
import ru.glavtoy.bankcardsystem.dto.AuthResponse;
import ru.glavtoy.bankcardsystem.dto.RegisterRequest;
import ru.glavtoy.bankcardsystem.entity.User;
import ru.glavtoy.bankcardsystem.repository.UserRepository;
import ru.glavtoy.bankcardsystem.util.JWTTokenUtil;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtTokenUtil.generateToken(user);
        return new AuthResponse(token);
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует!");
        }
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(List.of("USER"))
                .build();
        userRepository.save(user);
    }
}