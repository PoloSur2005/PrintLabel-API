-- ============================================================
--  PrintLabel Manager — Script de Base de Datos
--  MySQL 8.x
-- ============================================================

CREATE DATABASE IF NOT EXISTS printlabel_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_spanish_ci;

USE printlabel_db;

-- ------------------------------------------------------------
-- Tabla 1: usuarios
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario     INT           NOT NULL AUTO_INCREMENT,
    nombre         VARCHAR(100)  NOT NULL,
    email          VARCHAR(150)  NOT NULL,
    password_hash  VARCHAR(255)  NOT NULL,
    rol            ENUM('admin','operario') NOT NULL DEFAULT 'operario',
    activo         TINYINT(1)    NOT NULL DEFAULT 1,
    created_at     DATETIME      NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id_usuario),
    UNIQUE KEY uk_usuarios_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabla 2: fabricas
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS fabricas (
    id_fabrica  INT          NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(150) NOT NULL,
    ciudad      VARCHAR(100)     NULL,
    contacto    VARCHAR(100)     NULL,
    activo      TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (id_fabrica),
    UNIQUE KEY uk_fabricas_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabla 3: programas
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS programas (
    id_programa  INT          NOT NULL AUTO_INCREMENT,
    clave        VARCHAR(30)  NOT NULL,
    nombre       VARCHAR(150) NOT NULL,
    descripcion  TEXT             NULL,
    activo       TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (id_programa),
    UNIQUE KEY uk_programas_clave (clave)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabla 4: tallas
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tallas (
    id_talla      INT            NOT NULL AUTO_INCREMENT,
    numero_talla  DECIMAL(4,1)  NOT NULL,
    centimetros   DECIMAL(4,1)  NOT NULL,
    activo        TINYINT(1)    NOT NULL DEFAULT 1,
    PRIMARY KEY (id_talla),
    UNIQUE KEY uk_tallas_numero (numero_talla)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabla 5: ordenes
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ordenes (
    id_orden            INT          NOT NULL AUTO_INCREMENT,
    folio               VARCHAR(30)  NOT NULL,
    id_fabrica          INT          NOT NULL,
    id_usuario          INT          NOT NULL,
    fecha_programacion  DATE         NOT NULL,
    observaciones       TEXT             NULL,
    estatus             ENUM('borrador','cerrada','exportada') NOT NULL DEFAULT 'borrador',
    created_at          DATETIME     NOT NULL DEFAULT NOW(),
    updated_at          DATETIME     NOT NULL DEFAULT NOW() ON UPDATE NOW(),
    PRIMARY KEY (id_orden),
    UNIQUE KEY uk_ordenes_folio (folio),
    CONSTRAINT fk_ordenes_fabrica  FOREIGN KEY (id_fabrica) REFERENCES fabricas (id_fabrica),
    CONSTRAINT fk_ordenes_usuario  FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabla 6: orden_estilos
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS orden_estilos (
    id_estilo   INT  NOT NULL AUTO_INCREMENT,
    id_orden    INT  NOT NULL,
    id_programa INT  NOT NULL,
    orden_fila  INT  NOT NULL DEFAULT 0,
    PRIMARY KEY (id_estilo),
    CONSTRAINT fk_oe_orden    FOREIGN KEY (id_orden)    REFERENCES ordenes  (id_orden)    ON DELETE CASCADE,
    CONSTRAINT fk_oe_programa FOREIGN KEY (id_programa) REFERENCES programas (id_programa)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabla 7: estilo_tallas
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS estilo_tallas (
    id_detalle      INT  NOT NULL AUTO_INCREMENT,
    id_estilo       INT  NOT NULL,
    id_talla        INT  NOT NULL,
    cantidad_pares  INT  NOT NULL DEFAULT 0,
    PRIMARY KEY (id_detalle),
    UNIQUE KEY uk_estilo_talla (id_estilo, id_talla),
    CONSTRAINT fk_et_estilo FOREIGN KEY (id_estilo) REFERENCES orden_estilos (id_estilo) ON DELETE CASCADE,
    CONSTRAINT fk_et_talla  FOREIGN KEY (id_talla)  REFERENCES tallas         (id_talla)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Datos iniciales de catálogo
-- ============================================================

-- Tallas sistema mexicano con CM
INSERT INTO tallas (numero_talla, centimetros) VALUES
(13.0, 13.0), (13.5, 13.5), (14.0, 14.0), (14.5, 14.5),
(15.0, 15.0), (15.5, 15.5), (16.0, 16.0), (16.5, 16.5),
(17.0, 17.0), (17.5, 17.5), (18.0, 18.0), (18.5, 18.5),
(19.0, 19.0), (19.5, 19.5), (20.0, 20.0), (20.5, 20.5),
(21.0, 21.0), (21.5, 21.5), (22.0, 22.0), (22.5, 22.5),
(23.0, 23.0), (23.5, 23.5), (24.0, 24.0), (24.5, 24.5),
(25.0, 25.0), (25.5, 25.5), (26.0, 26.0), (26.5, 26.5),
(27.0, 27.0), (27.5, 27.5), (28.0, 28.0), (28.5, 28.5),
(29.0, 29.0), (29.5, 29.5), (30.0, 30.0)
ON DUPLICATE KEY UPDATE centimetros = VALUES(centimetros);

-- Usuario admin inicial (password: Admin2026!)
INSERT INTO usuarios (nombre, email, password_hash, rol, activo) VALUES
('Administrador', 'admin@printlabel.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh/i', 'admin', 1)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- Programas de ejemplo
INSERT INTO programas (clave, nombre) VALUES
('ANG-017', 'Angulo 017 Caballero'),
('ANG-018', 'Angulo 018 Dama'),
('CLT-001', 'Classic Tenis 001')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- Fábrica de ejemplo
INSERT INTO fabricas (nombre, ciudad, contacto) VALUES
('Planta Norte', 'León, Gto.', 'Juan Pérez')
ON DUPLICATE KEY UPDATE ciudad = VALUES(ciudad);
