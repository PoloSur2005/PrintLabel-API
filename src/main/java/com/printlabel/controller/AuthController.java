package com.printlabel.controller;

import com.printlabel.dto.AuthDto;
import com.printlabel.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/v1/auth/login
     * Inicia sesión y devuelve JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthDto.LoginResponse> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * POST /api/v1/auth/logout
     * Invalida el token activo.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String token = (String) request.getAttribute("jwtToken");
        if (token != null) {
            authService.logout(token);
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/auth/me
     * Devuelve el perfil del usuario autenticado.
     */
    @GetMapping("/me")
    public ResponseEntity<AuthDto.PerfilResponse> me(Authentication auth) {
        return ResponseEntity.ok(authService.getMe(auth.getName()));
    }
}
