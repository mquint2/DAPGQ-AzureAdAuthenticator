package co.com.bancodebogota.endpoint;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * <p>Esta clase permite centralizar la lectura a través de variables de entorno
 * de los endpoints de los servicios Rest que se usan.</p>
 * @author wriver1
 *
 */
@Component
public class EndpointService {

	@Value("#{systemEnvironment['AZURE_AUTHENTICATOR_ENDPOINT']}")
	private String AZURE_AUTHENTICATOR_ENDPOINT;
	
	@Value("#{systemEnvironment['TOKEN_GENERATOR_ENDPOINT']}")
	private String TOKEN_GENERATOR_ENDPOINT;
	
	@Value("#{systemEnvironment['LDAP_AUTHENTICATOR_ENDPOINT']}")
	private String LDAP_AUTHENTICATOR_ENDPOINT;
	
	@Value("#{systemEnvironment['GRAPH_AZURE_API_MEMBERS_OF']}")
	private String GRAPH_AZURE_API_MEMBERS_OF = "https://graph.microsoft.com/v1.0/me/memberOf";

	public EndpointService() {
	}

	public String getAZURE_AUTHENTICATOR_ENDPOINT() {
		return AZURE_AUTHENTICATOR_ENDPOINT;
	}

	public void setAZURE_AUTHENTICATOR_ENDPOINT(String aZURE_AUTHENTICATOR_ENDPOINT) {
		AZURE_AUTHENTICATOR_ENDPOINT = aZURE_AUTHENTICATOR_ENDPOINT;
	}

	public String getTOKEN_GENERATOR_ENDPOINT() {
		return TOKEN_GENERATOR_ENDPOINT;
	}

	public void setTOKEN_GENERATOR_ENDPOINT(String tOKEN_GENERATOR_ENDPOINT) {
		TOKEN_GENERATOR_ENDPOINT = tOKEN_GENERATOR_ENDPOINT;
	}

	public String getLDAP_AUTHENTICATOR_ENDPOINT() {
		return LDAP_AUTHENTICATOR_ENDPOINT;
	}

	public void setLDAP_AUTHENTICATOR_ENDPOINT(String lDAP_AUTHENTICATOR_ENDPOINT) {
		LDAP_AUTHENTICATOR_ENDPOINT = lDAP_AUTHENTICATOR_ENDPOINT;
	}

	public String getGRAPH_AZURE_API_MEMBERS_OF() {
		return GRAPH_AZURE_API_MEMBERS_OF;
	}

	public void setGRAPH_AZURE_API_MEMBERS_OF(String gRAPH_AZURE_API_MEMBERS_OF) {
		GRAPH_AZURE_API_MEMBERS_OF = gRAPH_AZURE_API_MEMBERS_OF;
	}
	
}
