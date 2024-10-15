package com.example.api.web.rest;




import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.api.domain.Address;
import com.example.api.domain.Customer;
import com.example.api.service.AddressService;
import com.example.api.service.CustomerService;

@RestController
@RequestMapping("/customers")
public class CustomerController {

	private CustomerService service;
	private final AddressService addressService;

	@Autowired
	public CustomerController(CustomerService service, AddressService addressService) {
		this.service = service;
		this.addressService = addressService;
	}

	@GetMapping
	public Page<Customer> findAll(@PageableDefault(size = 10) Pageable pageable) {
		return service.findAll(pageable);

	}

	@GetMapping("/{id}")
	public Customer findById(@PathVariable Long id) {
		return service.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Customer create(@Valid @RequestBody Customer customer) {
		return service.save(customer);
	}

	@PutMapping("/{id}")
	public Customer update(@PathVariable Long id, @Valid @RequestBody Customer updatedCustomer) {
		return service.findById(id).map(customer -> {
			customer.setName(updatedCustomer.getName());
			customer.setEmail(updatedCustomer.getEmail());
			return service.save(customer);
		}).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		Customer customer = service.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
		service.delete(customer);
	}

	// POST to add an address to a customer
	@PostMapping("/{id}/addresses")
	@ResponseStatus(HttpStatus.CREATED)
	public Customer addAddress(@PathVariable Long id, @Valid @RequestBody Address address) {
		return service.addAddress(id, address);
	}

	// PUT to update an address of a customer
	@PutMapping("/{customerId}/addresses/{addressId}")
	public Customer updateAddress(@PathVariable Long customerId, @PathVariable Long addressId,
			@Valid @RequestBody Address updatedAddress) {
		return service.updateAddress(customerId, addressId, updatedAddress);
	}

	// DELETE an address of a customer
	@DeleteMapping("/{customerId}/addresses/{addressId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAddress(@PathVariable Long customerId, @PathVariable Long addressId) {
		Customer customer = service.findById(customerId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
		Address address = customer.getAddresses().stream().filter(a -> a.getId().equals(addressId)).findFirst()
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));
		customer.getAddresses().remove(address);
		service.save(customer);
	}

	// POST adicionar endereço por CEP
	@PostMapping("/{id}/addresses/cep/{zipcode}")
	@ResponseStatus(HttpStatus.CREATED)
	public Customer addAddressByZipcode(@PathVariable Long id, @PathVariable String zipcode) {
		Address address = addressService.fetchAddressByZipcode(zipcode);
		return service.addAddress(id, address);
	}
}
