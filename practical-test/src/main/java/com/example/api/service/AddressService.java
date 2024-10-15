package com.example.api.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.example.api.domain.Address;
import com.example.api.dto.AddressViaCepResponse;

@Service
public class AddressService {

	private static final String VIA_CEP_URL = "https://viacep.com.br/ws/";

	public Address fetchAddressByZipcode(String zipcode) {
	    RestTemplate restTemplate = new RestTemplate();

	    AddressViaCepResponse response = restTemplate.getForObject(VIA_CEP_URL + zipcode + "/json",
	            AddressViaCepResponse.class);
	    
	    if (response == null) {
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid Zipcode");
	    }
	    
	    // Log a resposta da API
	    System.out.println("Resposta da API: " + response);
	    
	    if (response.getLogradouro() == null) {
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid Zipcode");
	    }

	    Address address = new Address();
	    address.setStreet(response.getLogradouro());
	    address.setCity(response.getLocalidade());
	    address.setZipcode(response.getCep());
	    return address;
	}
}