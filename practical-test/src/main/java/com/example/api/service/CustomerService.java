package com.example.api.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.api.domain.Address;
import com.example.api.domain.Customer;
import com.example.api.repository.CustomerRepository;

@Service
public class CustomerService {

	private CustomerRepository repository;

	@Autowired
	public CustomerService(CustomerRepository repository) {
		this.repository = repository;
	}

	public Page<Customer> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}

	public Optional<Customer> findById(Long id) {
		return repository.findById(id);
	}

	public Customer save(Customer customer) {
		return repository.save(customer);
	}

	public void delete(Customer customer) {
		repository.delete(customer);
	}

	public Customer updateAddress(Long customerId, Long addressId, Address updatedAddress) {
		Customer customer = findById(customerId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
		Address address = customer.getAddresses().stream().filter(a -> a.getId().equals(addressId)).findFirst()
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

		address.setStreet(updatedAddress.getStreet());
		address.setCity(updatedAddress.getCity());
		address.setZipcode(updatedAddress.getZipcode());
		return save(customer);
	}

	public Customer addAddress(Long customerId, Address address) {
		Customer customer = findById(customerId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

		address.setCustomer(customer);
		customer.getAddresses().add(address);
		return save(customer);
	}

}
