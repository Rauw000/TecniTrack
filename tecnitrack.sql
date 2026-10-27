-- Base de datos de TecniTrack
CREATE DATABASE IF NOT EXISTS tecnitrack
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE tecnitrack;

-- Tabla de roles
CREATE TABLE IF NOT EXISTS rol (
    id_rol BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    PRIMARY KEY (id_rol),
    UNIQUE KEY uk_rol_nombre (nombre)
);

-- Roles iniciales del sistema
INSERT INTO rol (nombre)
SELECT 'Cliente'
WHERE NOT EXISTS (
    SELECT 1 FROM rol WHERE nombre = 'Cliente'
);

INSERT INTO rol (nombre)
SELECT 'Técnico'
WHERE NOT EXISTS (
    SELECT 1 FROM rol WHERE nombre = 'Técnico'
);

INSERT INTO rol (nombre)
SELECT 'Administrador'
WHERE NOT EXISTS (
    SELECT 1 FROM rol WHERE nombre = 'Administrador'
);