package com.printlabel.service;

import com.printlabel.dto.AuthDto;
import com.printlabel.exception.GlobalExceptionHandler.UnauthorizedException;
import com.printlabel.model.Usuario;
import com.printlabel.repository.UsuarioRepository;
import com.printlabel.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));

        if (!usuario.getActivo()) {
            throw new UnauthorizedException("Usuario inactivo");
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        String token = jwtUtil.generateToken(usuario.getEmail());
        return new AuthDto.LoginResponse(token, usuario);
    }

    public void logout(String token) {
        jwtUtil.invalidateToken(token);
    }

    public AuthDto.PerfilResponse getMe(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));
        return new AuthDto.PerfilResponse(usuario);
    }
}
