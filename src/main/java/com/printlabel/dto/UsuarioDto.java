package com.printlabel.dto;

import com.printlabel.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

public class UsuarioDto {

    @Data
    public static class Request {
        @NotBlank(message = "El nombre es requerido")
        @Size(max = 100)
        private String nombre;

        @NotBlank(message = "El email es requerido")
        @Email(message = "Formato de email inválido")
        @Size(max = 150)
        private String email;

        @NotBlank(message = "La contraseña es requerida")
        @Size(min = 6, message = "La contraseña debe tener mínimo 6 caracteres")
        private String password;

        @NotNull(message = "El rol es requerido")
        private Usuario.Rol rol;
    }

    @Data
    public static class UpdateRequest {
        @NotBlank(message = "El nombre es requerido")
        @Size(max = 100)
        private String nombre;

        @NotBlank(message = "El email es requerido")
        @Email
        @Size(max = 150)
        private String email;

        private String password; // Opcional en edición

        @NotNull
        private Usuario.Rol rol;

        @NotNull
        private Boolean activo;
    }

    @Data
    public static class Response {
        private Integer idUsuario;
        private String nombre;
        private String email;
        private Usuario.Rol rol;
        private Boolean activo;
        private LocalDateTime createdAt;

        public Response(Usuario u) {
            this.idUsuario = u.getIdUsuario();
            this.nombre = u.getNombre();
            this.email = u.getEmail();
            this.rol = u.getRol();
            this.activo = u.getActivo();
            this.createdAt = u.getCreatedAt();
        }
    }
}
