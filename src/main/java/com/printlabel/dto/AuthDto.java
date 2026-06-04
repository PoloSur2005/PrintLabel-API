package com.printlabel.dto;

import com.printlabel.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class AuthDto {

    @Data
    public static class LoginRequest {
        @NotBlank(message = "El email es requerido")
        @Email(message = "Formato de email inválido")
        private String email;

        @NotBlank(message = "La contraseña es requerida")
        private String password;
    }

    @Data
    public static class LoginResponse {
        private String token;
        private String tipo = "Bearer";
        private Integer idUsuario;
        private String nombre;
        private String email;
        private Usuario.Rol rol;

        public LoginResponse(String token, Usuario usuario) {
            this.token = token;
            this.idUsuario = usuario.getIdUsuario();
            this.nombre = usuario.getNombre();
            this.email = usuario.getEmail();
            this.rol = usuario.getRol();
        }
    }

    @Data
    public static class PerfilResponse {
        private Integer idUsuario;
        private String nombre;
        private String email;
        private Usuario.Rol rol;
        private Boolean activo;

        public PerfilResponse(Usuario u) {
            this.idUsuario = u.getIdUsuario();
            this.nombre = u.getNombre();
            this.email = u.getEmail();
            this.rol = u.getRol();
            this.activo = u.getActivo();
        }
    }
}
