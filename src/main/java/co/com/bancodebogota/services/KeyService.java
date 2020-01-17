/**
 * @author mquint2
 * Clase encargada de obtener la llave publica y retornarla para que las aplicaciones front que hagan uso de ella 
 * cifren las credenciales de los usuarios
 */
package co.com.bancodebogota.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import co.com.bancodebogota.endpoint.EndpointService;

@Service
public class KeyService implements IKeyService {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private EndpointService endpoints;
	 
	@Autowired
	private LogService logService;
	 
	private static final String URI_KEY = "/key";

	/**
	 * @return La llave publica 
	 */
	@Override
	public String getPublicKey() {
		
		String publicKey = null;
		logService.manejarInfoLog("Generando la llave", this.getClass());
		try {
			publicKey = restTemplate.getForObject(this.endpoints.getAZURE_AUTHENTICATOR_ENDPOINT() + URI_KEY, String.class);
			logService.manejarInfoLog("Llave generado con exito", this.getClass());
		} catch(Exception e) {
			logService.manejarExceptionLog(this.getClass(), e, null, null);
			publicKey = restTemplate.getForObject(this.endpoints.getLDAP_AUTHENTICATOR_ENDPOINT() + URI_KEY,  String.class);
		} 
		return publicKey;
	}	
	
}
