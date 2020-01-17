package co.com.bancodebogota.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bancodebogota.aqsw.logs.kafka.model.CorrelationId;
import com.bancodebogota.aqsw.logs.kafka.model.CustomMessage;
import com.bancodebogota.aqsw.logs.kafka.producer.Log;
import com.bancodebogota.aqsw.logs.kafka.producer.LogSecurity;

import co.com.bancodebogota.model.ResponseModel;
import co.com.bancodebogota.services.KeyService;
import co.com.bancodebogota.services.LibrariesService;
import co.com.bancodebogota.services.LoginService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = RequestMethod.GET)
public class LoginController {

	@Autowired
	private LibrariesService librariesService;

	@Autowired
	private KeyService keyService;

	@Autowired
	private LoginService loginService;

	
	@Autowired
	private LogSecurity logSecurity;
	
	@Autowired
	private Log log;
	
	@GetMapping("/key")
	public String getPublicKey() {
		return keyService.getPublicKey();
	}

	@GetMapping("/azuread/login")
	public ResponseEntity<ResponseModel> loginAzure(@RequestHeader("Authorization") String credentials) {
		return loginService.authenticateAzureAD(credentials);
	}

	@GetMapping("/ldapext/login")
	public ResponseEntity<ResponseModel> loginLdap(@RequestHeader("Authorization") String credentials) {
		return loginService.authenticateLDAP(credentials);
	}

	@GetMapping("/getLibraries")
	public ArrayList<String> getLibraries() {
		return librariesService.getLibraries();
	}

	@GetMapping("/healthCheck")
	public String threeScaleTestPath() {
		return "If you're seeing this message, it means that everything is alright";
	}

}
