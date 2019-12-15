package com.vvelazquez.testspring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
	
	@Autowired
	private RestClientService restClientService;

	@GetMapping
	public String ejemploClienteRestConCertificado() {
		
		String resultado = "Intento fallido";
		try {
			resultado = restClientService.getResponse();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return resultado;
		
	}
	
}
