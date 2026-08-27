package com.gtalent.jdbc.controller;

import com.gtalent.jdbc.dto.LoginRequest;
import com.gtalent.jdbc.dto.RegisterRequest;
import com.gtalent.jdbc.entity.AppUser;
import com.gtalent.jdbc.repository.AppUserRepository;
import com.gtalent.jdbc.service.JwtService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody RegisterRequest request
    ) {

        AppUser user = new AppUser();

        user.setUsername(request.username());

        user.setPassword(
                passwordEncoder.encode(request.password())
        );

        appUserRepository.save(user);

        return ResponseEntity.ok("註冊成功");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody LoginRequest request
    ) {

        AppUser user = appUserRepository
                .findByUsername(request.username())
                .orElse(null);

        if (user == null) {
            return ResponseEntity
                    .status(401)
                    .body("帳號或密碼錯誤");
        }

        boolean passwordMatches = passwordEncoder.matches(
                request.password(),
                user.getPassword()
        );

        if (!passwordMatches) {
            return ResponseEntity
                    .status(401)
                    .body("帳號或密碼錯誤");
        }

        String token = jwtService.generateToken(user.getUsername());

        return ResponseEntity.ok(token);
    }
}