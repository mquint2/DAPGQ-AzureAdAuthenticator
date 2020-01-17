package co.com.bancodebogota.security;

public class Constants {

	// Spring Security
	public static final String HEADER_AUTHORIZACION_KEY = "Authorization";
	public static final String TOKEN_BEARER_PREFIX = "Bearer ";
	
	// Componentes
	public static final String REPORTE_PREDETERMINADO = "ROLE_0101";
	public static final String REPORTE_ESTANDAR = "ROLE_0102";
	public static final String REPORTE_PERSONALIZADO = "ROLE_0103";
	public static final String CARGAR_INFORMACION_PAGOS = "ROLE_0201";
	public static final String SELECCIONAR_FORMA_PAGO = "ROLE_0301";
	public static final String AUTORIZAR_PAGO = "ROLE_0401";
	public static final String INFORMAR_NOVEDAD = "ROLE_0501";
	public static final String USUARIOS = "ROLE_0601";
	public static final String ADMINISTRADOR = "ROLE_ADMIN";

	public static final String HEADER_SECUENCIA_TRANSACCION = "TRANSACTION_SECUENCE";
}
