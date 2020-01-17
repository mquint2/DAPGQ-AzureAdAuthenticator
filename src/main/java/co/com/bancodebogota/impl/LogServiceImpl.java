package co.com.bancodebogota.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bancodebogota.aqsw.logs.kafka.model.CorrelationId;
import com.bancodebogota.aqsw.logs.kafka.model.CustomMessage;
import com.bancodebogota.aqsw.logs.kafka.producer.Log;
import com.bancodebogota.aqsw.logs.kafka.producer.LogSecurity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.com.bancodebogota.dto.ObjetoEnvioServicioDTO;
import co.com.bancodebogota.services.LogService;

/**
 * © Todos los derechos reservados Banco de Bogotá
 * <p>
 * Clase que implementa el manejo de logs para el servicio de Data Archivo Pago.
 * 
 * @author Javier Virviescas Toledo
 */
@Service
public class LogServiceImpl implements LogService
{
	
	/*
	 * Atributos
	 */

	@Autowired
	private Log log;

	@Autowired
	private LogSecurity logSecurity;

	private CorrelationId correlationId = null;
	
	@Autowired
	private HttpServletRequest context;

	@Value("${log.parametro.transaccionCodigo}")
	private String transaccionCodigo;
	
	@Value("${log.parametro.transaccionCanal}")
	private String transaccionCanal;

	@Value("${log.parametro.transaccionFiller}")
	private String transaccionFiller;

	@Value("${log.parametro.transaccionAplicacion}")
	private String transaccionAplicacion;
	
	@Value("${log.parametro.errorGenerico}")
	private StringBuilder errorGenerico;
	
	public static final String HEADER_SECUENCIA_TRANSACCION = "TRANSACTION_SECUENCE";
	/*
	 * Métodos implementados
	 */
	
	/**
	 * Método que inicializa el atributo correlationId cuando sea instanciado el servicio del log.
	 */
	@PostConstruct
	public void init()
	{
		correlationId = new CorrelationId (transaccionCodigo, transaccionCanal, transaccionFiller, transaccionAplicacion, UUID.randomUUID().toString().substring(0, 5));
	
	}
	
	/**
	 * @see com.bancodebogota.gciades.portallibranzas.LogService.service.LogCargaArchivoService
	 */
	@Override
	public void manejarExceptionLog(Class<?> clase, Exception exception, Object request, Object response)
	{
		String res = response!=null ? objetoToJson(response) : null;
		String req = response!=null ? objetoToJson(request) : null;
		log.error("[Hostname: " + this.getHostname() + "] " + errorGenerico, correlationId, clase, exception, req, res);
	}
	
	
	/**
	 * @see com.bancodebogota.gciades.portallibranzas.reporteals.service.LogService.service.LogCargaArchivoService
	 */
	@Override
	public void manejarInfoLog(String mensaje, Class<?> clase)
	{
		log.info(mensaje, correlationId, clase);
	}
	
	@Override
	public void manejarWarningLog(String mensaje, Class<?> clase,Exception e, String estadoResponse, Object request) {
		    log.warn(mensaje, correlationId, clase, e , estadoResponse, request );
	}	
	
	/**
	 * @see com.bancodebogota.gciades.portallibranzas.reporteals.service.LogService.service.LogCargaArchivoService
	 */
	@Override
	public void manejarLogSeguridad(ObjetoEnvioServicioDTO objetoEnvioServicios, Object response)
	{
		CustomMessage<Object, Object> mensaje = datosLogSeguridad(objetoEnvioServicios, response);
		envioLog(mensaje);
	}

	/**
	 * Método que retorna el hostname del contexto desde donde se está ejecutando la aplicación
	 * 
	 * @return Nombre del host
	 */
	private String getHostname()
	{
		String hostname = null;
		try
		{
			//hostname = StringUtils.EMPTY;
			hostname = InetAddress.getLocalHost().getHostName();
		}
		catch (UnknownHostException e)
		{
			hostname = "Hostname no encontrado";
		}
		return hostname;
	}
	
	/**
	 * Metodo que para convertir un objeto a un string JSON
	 * 
	 * @param o Objeto a transformar en JSON
	 * @return Cadena de String para crear un objeto JSON
	 */
	private String objetoToJson(Object o)
	{
		ObjectMapper mapper = new ObjectMapper();
		String json = "";
		try
		{
			json = mapper.writeValueAsString(o);
		}
		catch (JsonProcessingException e)
		{
			json = "No se pudo convertir el objeto response a JSON";
		}
		return json;
	}



	@Override
	public void correlationIdRefresh(CorrelationId correlationId) {
		correlationId = new CorrelationId(transaccionCodigo, transaccionCanal, transaccionFiller, transaccionAplicacion, UUID.randomUUID().toString().substring(0, 5));
	}
	
	@Override
	public CorrelationId getCorrelationId()
	{
		return correlationId;
	}
	
	@Override
	public String setTransactionSequence(String transactionCode)
	{
		String secuenciaTransaccion = context.getHeader(HEADER_SECUENCIA_TRANSACCION);
		this.correlationIdRefresh(transactionCode);
		if(secuenciaTransaccion!=null && !secuenciaTransaccion.equals("")) {
			this.getCorrelationId().setSecuenciaTransaccion(secuenciaTransaccion);
		} else {
			secuenciaTransaccion = this.getCorrelationId().getSecuenciaTransaccion();
		}
		return secuenciaTransaccion;
	}
	
	private CustomMessage<Object, Object> datosLogSeguridad(ObjetoEnvioServicioDTO objetoEnvioServicios, Object response)
	{
		CustomMessage<Object, Object> mensaje = new CustomMessage<>();
		String req = objetoToJson(objetoEnvioServicios);
		String res = response!=null ? objetoToJson(response) : null;
		mensaje.setCorrelationId(correlationId);
		mensaje.setCanal(transaccionCanal);
		mensaje.setIdentificacionFuncionario(objetoEnvioServicios.getUsuario());
		mensaje.setEstado(res);
		mensaje.setData(req);
		return mensaje;
	}

	private void envioLog(CustomMessage<Object, Object> mensaje)
	{
		logSecurity.logSeguridad(mensaje);
	}

	@Override
	public void correlationIdRefresh(String transaccionCodigo) {
		correlationId = new CorrelationId(transaccionCodigo, transaccionCanal, transaccionFiller, transaccionAplicacion, UUID.randomUUID().toString().substring(0, 5));
	}
}
