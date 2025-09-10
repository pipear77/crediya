-- Crear base de datos
CREATE DATABASE loan_db;

-- Conectarse a la base
\c loan_db;

-- Crear tabla tipo_prestamo
CREATE TABLE tipo_prestamo (
    id UUID PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    monto_minimo NUMERIC(15,2) NOT NULL,
    monto_maximo NUMERIC(15,2) NOT NULL,
    tasa_interes NUMERIC(5,2) NOT NULL,
    aprobacion_automatica BOOLEAN NOT NULL DEFAULT FALSE
);

-- Crear tabla solicitud_prestamo
CREATE TABLE solicitud_prestamo (
    id UUID PRIMARY KEY,
    documento_identidad VARCHAR(15) NOT NULL,
    correo VARCHAR(100), -- 📧 contacto del solicitante
    nombre VARCHAR(100), -- 👤 nombre del solicitante
    monto_solicitado NUMERIC(20,2) NOT NULL,
    plazo_meses INTEGER NOT NULL,
    id_tipo_prestamo UUID NOT NULL,
    tipo_prestamo VARCHAR(50), -- 🏦 nombre legible del tipo de préstamo
    tasa_interes NUMERIC(5,2), -- 📈 interés aplicado
    salario_base NUMERIC(20,2), -- 💰 ingreso base del solicitante
    monto_mensual_solicitud NUMERIC(20,2), -- 📆 cuota mensual estimada
    estado VARCHAR(20) NOT NULL,
    canal VARCHAR(100),
    fecha_creacion TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- 🔗 Clave foránea
    CONSTRAINT fk_tipo_prestamo FOREIGN KEY (id_tipo_prestamo) REFERENCES tipo_prestamo(id)
);


-- Insertar datos en tipo_prestamo
INSERT INTO tipo_prestamo (id, nombre, monto_minimo, monto_maximo, tasa_interes, aprobacion_automatica) VALUES
('b3f1c2e4-9a2d-4d7e-8c3a-2a1f5e6d9f1a', 'Crédito Personal', 1000000.00, 10000000.00, 1.50, TRUE),
('a1e2d3f4-5b6c-7d8e-9f01-23456789abcd', 'Crédito Educativo', 500000.00, 5000000.00, 1.20, FALSE);
