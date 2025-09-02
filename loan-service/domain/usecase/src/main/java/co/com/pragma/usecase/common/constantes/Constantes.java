package co.com.pragma.usecase.common.constantes;

public class Constantes {
    private Constantes() {}

    // Mensajes de error
    public static final String TIPO_PRESTAMO_NO_ENCONTRADO = "Tipo de préstamo no encontrado";
    public static final String ESTADO_PRESTAMO_NO_ENCONTRADO = "Estado del préstamo no encontrado";
    public static final String USUARIO_NO_ENCONTRADO = "Usuario no encontrado";

    // Estados de préstamo
    public static final String ESTADO_PRESTAMO_PENDIENTE_REVISION = "PENDIENTE DE REVISIÓN";

    // Mensajes de validación
    public static final String ERROR_DOCUMENTO_REQUERIDO = "El número de documento es obligatorio";
    public static final String ERROR_TIPO_REQUERIDO = "El tipo es obligatorio";
    public static final String ERROR_PLAZO_REQUERIDO = "El plazo del documento es obligatorio";
    public static final String ERROR_MONTO_REQUERIDO = "El monto es obligatorio";

    // Códigos de error
    public static final int CODIGO_NO_ENCONTRADO = 404;

}
