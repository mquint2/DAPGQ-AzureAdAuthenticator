package co.com.bancodebogota.services;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.com.bancodebogota.endpoint.EndpointService;

@Service
public class GroupService {
	
	@Autowired
	private EndpointService endpoints;
	
	@Autowired
	private LogService logService;
	
	public List<String> getGroups(String token) {
		RestTemplate restTemplate = new RestTemplate();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonResponse = null;
		List<String> groups = new ArrayList<>(); 
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", token);		
		ResponseEntity<String> response = null;
		HttpEntity<Object> request = new HttpEntity<Object>(headers);
		logService.manejarInfoLog("Consultando grupos con token", this.getClass());
		response =restTemplate.exchange(this.endpoints.getGRAPH_AZURE_API_MEMBERS_OF(), HttpMethod.GET, request, String.class);
		try {
			jsonResponse = mapper.readTree(response.getBody());			
			for(int i = 0; i < jsonResponse.findValue("value").size();i++) {
				groups.add(jsonResponse.findValue("value").get(i).get("displayName").asText());
			}			
		} catch (IOException e) {
			logService.manejarExceptionLog(this.getClass(), e, request, response);
		}
		return groups;
		
	}
	

}