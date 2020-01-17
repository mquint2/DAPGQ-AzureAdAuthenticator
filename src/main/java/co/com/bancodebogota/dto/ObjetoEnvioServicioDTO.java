package co.com.bancodebogota.dto;

import java.io.Serializable;
import java.util.Map;

import org.springframework.http.HttpMethod;

/**
 * © Todos los derechos reservados Banco de Bogotá
 * <p>
 * Clase utilizada para representar un consumo a un servicio REST y que se persiste finalmente en el log de la aplicación.
 * 
 * @author Javier Virviescas Toledo
 */
public class ObjetoEnvioServicioDTO implements Serializable
{
	
	/*
	 * Atributos
	 */
	
	private static final long serialVersionUID = -8526434119526103607L;

	private String url;

	private HttpMethod httpMethod;

	private Map<String, Object> valores;
	
	private Object body;
	
	private String usuario;
	
	
	
	public ObjetoEnvioServicioDTO(String url, HttpMethod httpMethod, Map<String, Object> valores, Object body,
			String usuario) {
		super();
		this.url = url;
		this.httpMethod = httpMethod;
		this.valores = valores;
		this.body = body;
		this.usuario = usuario;
	}

	/*
	 * Métodos get y set
	 */

	public String getUrl()
	{
		return url;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public HttpMethod getHttpMethod()
	{
		return httpMethod;
	}

	public void setHttpMethod(HttpMethod httpMethod)
	{
		this.httpMethod = httpMethod;
	}

	public Map<String, Object> getValores()
	{
		return valores;
	}

	public void setValores(Map<String, Object> valores)
	{
		this.valores = valores;
	}
	
	public Object getBody()
	{
		return body;
	}

	public void setBody(Object body)
	{
		this.body = body;
	}

}
