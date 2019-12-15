package com.vvelazquez.testspring;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class RestClientService {

	private static final String URL = "https://vvelazquez.com:8443/ejemplo3-0.0.1";
	private static final String PATH_CERTIFICADO = "/home/brm07620/vvelazquez_certificados/cudti/";
	private static final String CERTIFICADO = "cudti.p12";
	private static final String PASS_CERTIFICADO = "12345678";

	public String getResponse() throws Exception {

		SSLContext sslcontext = null;
		TrustManager[] trustManagers = null;
		KeyManager[] keyManagers = null;
		CredentialsProvider credsProvider = null;
		CloseableHttpClient httpClient = null;

		HttpHeaders headers = new HttpHeaders();

		sslcontext = SSLContext.getInstance("TLS");
		keyManagers = getKeyManagers("pkcs12", new FileInputStream(new File(PATH_CERTIFICADO + CERTIFICADO)),
				PASS_CERTIFICADO);
		sslcontext.init(keyManagers, trustManagers, new SecureRandom());

		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1.2" }, null,
				SSLConnectionSocketFactory.getDefaultHostnameVerifier());

		// AUTH BASIC
		credsProvider = new BasicCredentialsProvider();
		httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).setSSLSocketFactory(sslsf)
				.build();

		RestTemplate restTemplate = new RestTemplate();
		String json = "";

		UriComponents uriComponents = UriComponentsBuilder.fromUriString(URL).build().encode();
		URI uri2 = uriComponents.toUri();

		HttpEntity<String> request = new HttpEntity<String>(headers);
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setReadTimeout(100000);
		requestFactory.setHttpClient(httpClient);
		restTemplate.setRequestFactory(requestFactory);
		ResponseEntity<String> response = restTemplate.exchange(uri2, HttpMethod.GET, request, String.class);

		json = response.getBody();

		return json;
	}

	private static KeyManager[] getKeyManagers(String keyStoreType, InputStream keyStoreFile, String keyStorePassword)
			throws Exception {
		KeyStore keyStore = KeyStore.getInstance(keyStoreType);
		keyStore.load(keyStoreFile, keyStorePassword.toCharArray());
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(keyStore, keyStorePassword.toCharArray());
		return kmf.getKeyManagers();
	}

}
