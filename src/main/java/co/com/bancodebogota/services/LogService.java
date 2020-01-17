package co.com.bancodebogota.services;

import com.bancodebogota.aqsw.logs.kafka.model.CorrelationId;

import co.com.bancodebogota.dto.ObjetoEnvioServicioDTO;

/**
 * © Todos los derechos reservados Banco de Bogotá
 * <p>
 * Interface que define los métodos que debe tener el servicio de manejo de logs.
 * 
 * @author Javier Virviescas Toledo
 */
public interface LogService
{
	
	/**
	 * Método que tiene la lógica para el manejo de logs cuando ocurre una exception en la aplicación
	 * 
	 * @param clase Clase desde donde se esta consumiendo el método
	 * @param exception Exception presentada y que pretende guardarse en el log
	 * @param request Datos enviados cuando se realiza el consumo a otro servicio
	 * @param response Respuesta que da el servicio al consumo que se envia en el request
	 */
	public void manejarExceptionLog(Class<?> clase, Exception exception, Object request, Object response);
	
	/**
	 * Método para enviar advertencias a los logs cuando ocurre una excepciones condicionales o intentos recurrentes 
	 * al loguearse.
	 * @param mensaje
	 * @param objetoEnvioServicios
	 * @param clase
	 * @param e
	 * @param estadoResponse
	 * @param request
	 */
	
	public void manejarWarningLog(String mensaje, Class <?> clase, Exception e, String estadoResponse, Object request);
	
	/**
	 * Método para manejar los logs informativos de la aplicación
	 * 
	 * @param mensaje Texto informativo que se quiere guardar en el log
	 * @param clase Clase desde donde se esta consumiendo el método
	 */
	public void manejarInfoLog(String mensaje, Class<?> clase);
	
	/**
	 * Método para manejar el log de seguridad de la aplicación
	 *
	 * @param mensaje Objeto que solicita el log personalizado del componente transversal
	 * @param objetoEnvioServicios Datos enviados cuando se realiza el consumo a otro servicio
	 * @param response Respuesta que da el servicio al consumo del servicio que se envia en el request. 
	 */
	public void manejarLogSeguridad(ObjetoEnvioServicioDTO objetoEnvioServicios, Object response);
	
	/**
	 * Método que inicializa el atributo correlationId con un codigo de transaccion especifico.
	 */
	public void correlationIdRefresh(String transaccionCodigo);
	
	/**
	 * Método que inicializa correlationId cuando ya se cuente con uno.
	 */
	public void correlationIdRefresh(CorrelationId correlationId);
	
	public CorrelationId getCorrelationId();
	
	public String setTransactionSequence(String transactionCode);

}
