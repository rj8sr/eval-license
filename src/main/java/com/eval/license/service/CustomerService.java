package com.eval.license.service;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.web.bind.annotation.PathVariable;

import com.eval.license.model.Customers;
import com.eval.licenses.request.CustomersRequest;

public interface CustomerService {
	
	public Customers createCustomer(@Valid CustomersRequest customerRequest);

	public List<Customers> findAll();

	public Optional<Customers> getCustomerById(Long id);

	public Customers updateCustomer(@PathVariable @Min(1) Long customerId, @Valid CustomersRequest customerRequest);
}
