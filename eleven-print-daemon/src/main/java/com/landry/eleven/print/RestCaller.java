package com.landry.eleven.print;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class RestCaller {

	Logger logger = LoggerFactory.getLogger(RestCaller.class);

	@Value("${username}")
	String username;

	@Value("${password}")
	String password;

	private RestTemplate restTemplate = new RestTemplate();
	private HttpEntity<String> request;


	public RestCaller() {
		super();
	}

//	@PostConstruct
	private void setUpAuthentication() {
	    String plainCreds = username+":"+password;
	    byte[] plainCredsBytes = plainCreds.getBytes();
	    byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
	    String base64Creds = new String(base64CredsBytes);

	    HttpHeaders headers = new HttpHeaders();
	    headers.add("Authorization", "Basic " + base64Creds);
	    request = new HttpEntity<String>(headers);
	    
	}

	Object[] getList(String uri, Class<?> clazz) {
//	    ResponseEntity<?> response = restTemplate.exchange(uri, HttpMethod.GET, request, clazz);
	    ResponseEntity<?> response = restTemplate.getForEntity(uri, clazz);
	    HttpStatus statusCode = response.getStatusCode();
	    informUser(uri, statusCode);
	    return (Object[])response.getBody();
	}

	private void informUser(String uri, HttpStatus statusCode) {
	    logger.debug("The http statusCode: " + statusCode + " was returned for REST call \n"
	    		+ uri );
	}

	Object get(String uri, Class<?> clazz) {
//	    ResponseEntity<?> response = restTemplate.exchange(uri, HttpMethod.GET, request, clazz);
	    ResponseEntity<?> response = restTemplate.getForEntity(uri, clazz);
	    HttpStatus statusCode = response.getStatusCode();
	    informUser(uri, statusCode);
	    return response.getBody();
	}

	public void delete(String uri) {
		logger.info("Here is the REST call\n" + uri);
	    restTemplate.delete(uri);
	}

}
