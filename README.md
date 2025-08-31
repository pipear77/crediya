# crediya
Aplicación para realizar pequeños préstamos

[HU1] Registro de usuarios ──► [Base de datos AUTENTICACION]
                                │
                                ▼
[HU2] Solicitud de préstamos ──► [UsuarioWebClientAdapter] ──► [Consulta a AUTENTICACION]
                                │
                                ▼
[HU3] Autenticación ──► [JWT + Roles] ──► [Protección de endpoints HU1 y HU2]
