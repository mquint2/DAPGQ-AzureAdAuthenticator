package co.com.bancodebogota.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.com.bancodebogota.dto.ObjetoEnvioServicioDTO;
import co.com.bancodebogota.endpoint.EndpointService;
import co.com.bancodebogota.model.ResponseModel;

@Service
public class LoginService {
	
	@Autowired
	private EndpointService endpoints;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private GroupService groupService;
	
	@Autowired
	private LogService logService;
	
	ObjetoEnvioServicioDTO objetoEnvioServicios = null;
	
	public ResponseEntity<ResponseModel> authenticateAzureAD(String credentials) {
		String input = credentials;
		String token=null;
		String usuario="";
		ResponseModel responseModel = new ResponseModel();
		HttpHeaders headers = new HttpHeaders();
		HttpHeaders responseHeaders = new HttpHeaders();
		
		Map<String,Object> valores= new HashMap<>();
		headers.add("Authorization", input);
		
		HttpEntity<Object> req = new HttpEntity<Object>(headers);
		ResponseEntity<String> res = null;
		
		try {
			res = restTemplate.exchange( this.endpoints.getAZURE_AUTHENTICATOR_ENDPOINT() +"/login", HttpMethod.GET, req, String.class);
			usuario= res.getHeaders().getFirst("Username");
			valores.put("login", "El usuario "+ usuario +" esta intentando autenticarse");
			
	 		String aadToken = res.getHeaders().getFirst("Authorization");
			String groups=StringUtils.collectionToDelimitedString(groupService.getGroups(aadToken), "|");
			
			if(res.getStatusCode().equals(HttpStatus.OK)) {
				valores.put("login exitoso", "El usuario "+ usuario +" se logueó exitosamente");
				token=getToken(res.getHeaders().getFirst("Username"), groups);
				responseModel.setAuthorization(token);
				responseModel.setPublicKey(getPublicKey());
				responseModel.setMessage("Usuario autenticado con éxito");
				responseHeaders.add("Authorization", token);
				responseHeaders.add("PublicKey", getPublicKey());
				responseHeaders.add("Access-Control-Allow-Headers", "*");
			}		
			
		} catch (RestClientException rce) {
			if (rce instanceof HttpClientErrorException) {
				HttpClientErrorException clientEx = ((HttpClientErrorException)rce);
				if(clientEx.getStatusCode() == HttpStatus.UNAUTHORIZED){
					valores.put("login errado", "Usuario o contraseña errado");
					responseModel.setMessage("Usuario o contraseña errado");
					logService.manejarExceptionLog(this.getClass(), rce, req, res);
					return new ResponseEntity<ResponseModel>(responseModel, HttpStatus.UNAUTHORIZED);
				}else {
					valores.put("login errado", "Ha ocurrido un error autenticando al usuario");
					responseModel.setMessage("Ha ocurrido un error autenticando al usuario");
					logService.manejarExceptionLog(this.getClass(), rce, req, res);
					return new ResponseEntity<ResponseModel>(responseModel, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}else {
				responseModel.setMessage("Ha ocurrido un error autenticando al usuario");
				logService.manejarExceptionLog(this.getClass(), rce, req, res);
				return new ResponseEntity<ResponseModel>(responseModel, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			responseModel.setMessage("Ha ocurrido un error autenticando al usuario");
			logService.manejarExceptionLog(this.getClass(), e, req, res);
			return new ResponseEntity<ResponseModel>(responseModel, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		valores.put("login exitoso", "El usuario "+ usuario +" se logueó exitosamente");
		objetoEnvioServicios = new ObjetoEnvioServicioDTO(this.endpoints.getAZURE_AUTHENTICATOR_ENDPOINT() +"/login", HttpMethod.GET, valores, res!=null ? res.getBody() : null, usuario);
		logService.manejarLogSeguridad(objetoEnvioServicios, res);	
		
		return new ResponseEntity<ResponseModel>(responseModel, responseHeaders, HttpStatus.OK);
	}
	
	public ResponseEntity<ResponseModel> authenticateLDAP(String credentials) {
		String token= null;
		String username = null;
		List<String> groupsArray = new ArrayList<>();
		ResponseModel responseModel = new ResponseModel();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonresponse = null;
		HttpHeaders headers = new HttpHeaders();
		HttpHeaders responseHeaders = new HttpHeaders();
		headers.add("Authorization", credentials);
		HttpEntity<Object> req = new HttpEntity<Object>(headers);
		ResponseEntity<JsonNode> res = null;
		Map<String,Object> valores= new HashMap<>();
		try {			
			res = restTemplate.exchange( this.endpoints.getLDAP_AUTHENTICATOR_ENDPOINT() + "/ldapextlogin", HttpMethod.GET,req, JsonNode.class);
			
			if(res.getStatusCode().equals(HttpStatus.OK)) {
				if(!res.getBody().findValue("response").findValue("sAMAccountName").isNull()) {
					username = res.getBody().findValue("response").findValue("sAMAccountName").asText();
				}
				if(!res.getBody().findValue("response").findValue("memberOf").isNull()) {
					for(int i = 0; i < res.getBody().findValue("response").findValue("memberOf").size(); i++) {
						groupsArray.add(res.getBody().findValue("response").findValue("memberOf").get(i).asText().split(",")[0].replace("CN=", ""));
						
					}
					valores.put("loginGro", "el usuario "+username+" tiene grupos asignados");
				}
				token = getToken(username, StringUtils.collectionToDelimitedString(groupsArray, "|"));
				responseModel.setAuthorization(token);
				responseModel.setPublicKey(getPublicKey());
				responseModel.setMessage("Usuario" + username +" fué autenticado con éxito");
				responseHeaders.add("Authorization", "Bearer "+ token);
				responseHeaders.add("PublicKey", getPublicKey());
				valores.put("login", "el usuario "+username+" se logueó con exito");
			}		
			objetoEnvioServicios = new ObjetoEnvioServicioDTO(this.endpoints.getLDAP_AUTHENTICATOR_ENDPOINT() + "/ldapextlogin", HttpMethod.GET, valores, res!=null ? res.getBody() : null, username);
			logService.manejarLogSeguridad(objetoEnvioServicios, res);
		}catch (HttpClientErrorException hce) {
			try {
				jsonresponse = mapper.readTree(hce.getResponseBodyAsString());
			} catch (IOException e) {
				logService.manejarExceptionLog(this.getClass(), e, hce, jsonresponse);					
				responseModel.setMessage("Ha ocurrido un error autenticando al usuario");
				return new ResponseEntity<ResponseModel>(responseModel, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			if(hce.getStatusCode() != null ) {
				if(hce.getResponseHeaders() != null) {
					username = hce.getResponseHeaders().get("Username") != null && !hce.getResponseHeaders().get("Username").isEmpty() ? hce.getResponseHeaders().get("Username").get(0): null;
					responseHeaders.add("Username", username);
					responseModel.setUsername(username);
				}				
				responseModel.setMessage(jsonresponse.get("message").asText());
				logService.manejarExceptionLog(this.getClass(), hce, hce.getStatusCode() , responseModel);
				return new ResponseEntity<ResponseModel>(responseModel,responseHeaders, hce.getStatusCode());
			} else {
				logService.manejarExceptionLog(this.getClass(), hce, hce.getStatusCode() , responseModel);
				responseModel.setMessage(jsonresponse.get("message").asText());
				return new ResponseEntity<ResponseModel>(responseModel, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}catch (Exception e) {
			logService.manejarExceptionLog(this.getClass(), e, req , responseModel);				
			responseModel.setMessage("Error no esperado autenticando al usuario. " + e.getClass().getName());
			return new ResponseEntity<ResponseModel>(responseModel, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		valores.put("login exitoso", "El usuario "+ username +" se logueó exitosamente");
		objetoEnvioServicios.setValores(valores);
		logService.manejarLogSeguridad(objetoEnvioServicios, res);
		return new ResponseEntity<ResponseModel>(responseModel, responseHeaders, HttpStatus.OK);		
	}
	
	private String getToken(String username, String groups) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Groups", groups);
		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(this.endpoints.getTOKEN_GENERATOR_ENDPOINT() +"/getToken"+"/"+username, HttpMethod.GET, entity, String.class);
		return response.getBody();
	}

	public String getPublicKey() {
		String publicKey = restTemplate.getForObject(this.endpoints.getTOKEN_GENERATOR_ENDPOINT()+"/getPublicKey", String.class);
		return publicKey;
	}
	
}