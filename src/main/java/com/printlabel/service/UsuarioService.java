package com.printlabel.service;

import com.printlabel.dto.UsuarioDto;
import com.printlabel.exception.GlobalExceptionHandler.*;
import com.printlabel.model.Usuario;
import com.printlabel.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioDto.Response> findAll() {
        return usuarioRepository.findAllByActivoTrue()
                .stream()
                .map(UsuarioDto.Response::new)
                .collect(Collectors.toList());
    }

    public UsuarioDto.Response findById(Integer id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        return new UsuarioDto.Response(u);
    }

    public UsuarioDto.Response create(UsuarioDto.Request request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Ya existe un usuario con el email: " + request.getEmail());
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol());
        usuario.setActivo(true);

        return new UsuarioDto.Response(usuarioRepository.save(usuario));
    }

    public UsuarioDto.Response update(Integer id, UsuarioDto.UpdateRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        // Verificar email único si cambió
        if (!usuario.getEmail().equals(request.getEmail()) && usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Ya existe un usuario con el email: " + request.getEmail());
        }

        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setRol(request.getRol());
        usuario.setActivo(request.getActivo());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        return new UsuarioDto.Response(usuarioRepository.save(usuario));
    }

    public void delete(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }
}
