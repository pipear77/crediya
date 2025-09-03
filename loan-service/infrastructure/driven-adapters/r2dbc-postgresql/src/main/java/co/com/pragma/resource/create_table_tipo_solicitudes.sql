CREATE TABLE tipo_solicitudes (
  id VARCHAR(36) PRIMARY KEY,
  nombre VARCHAR(255) NOT NULL,
  min_monto NUMERIC(14, 2) NOT NULL,
  max_monto NUMERIC(14, 2) NOT NULL,
  ratio_interes DOUBLE PRECISION NOT NULL,
  aprobacion BOOLEAN NOT NULL
);
