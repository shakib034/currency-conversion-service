package com.shak.microservices;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
class CurrencyConversionController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	CurrencyExchangeServiceProxy proxy;
	
	@GetMapping("currency-converter/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrency(@PathVariable String from, 
			@PathVariable String to, 
			@PathVariable BigDecimal quantity) {
		
		// Feign client
		Map<String, String> uriVariables = new HashMap<>();		
		uriVariables.put("from", from);
		uriVariables.put("to", to);
		
		ResponseEntity<CurrencyConversionBean> responseEntity = new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}",
				CurrencyConversionBean.class, uriVariables);
		CurrencyConversionBean body = responseEntity.getBody();
		logger.info("{}", body);
		System.out.println(body.toString());
		return new CurrencyConversionBean(body.getId(), from, to, body.getConversionMultiple(), quantity, 
				quantity.multiply(body.getConversionMultiple()), body.getPort());

	}
	
	@GetMapping("currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrencyFeign(@PathVariable String from, 
			@PathVariable String to, 
			@PathVariable BigDecimal quantity) {
		

		CurrencyConversionBean body = proxy.retrieveExchangeValue(from, to);
		logger.info("{}", body);
		System.out.println(body.toString());
		return new CurrencyConversionBean(body.getId(), from, to, body.getConversionMultiple(), quantity, 
				quantity.multiply(body.getConversionMultiple()), body.getPort());

	}

}
