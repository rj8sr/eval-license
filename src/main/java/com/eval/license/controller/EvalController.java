package com.eval.license.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eval.license.model.Customers;
import com.eval.license.service.impl.CustomerServiceImpl;
import com.eval.licenses.request.CustomersRequest;
import com.eval.licenses.response.BaseApiResponse;
import com.eval.licenses.response.ResponseBuilder;

@RestController
@RequestMapping("/api")
@Transactional
public class EvalController {

	private static final Logger log = LoggerFactory.getLogger(EvalController.class);

	@Autowired
	CustomerServiceImpl customerService;

	@GetMapping("/getAllCustomers")
	public ResponseEntity<BaseApiResponse> getAllCustomers() {
		log.debug("Get All Customers Method Started");
		BaseApiResponse baseApiResponse = null;
		List<Customers> customers = customerService.findAll();
		if (customers.size() > 0) {
			baseApiResponse = ResponseBuilder.getSuccessResponse(customers);
			return new ResponseEntity<>(baseApiResponse, HttpStatus.OK);
		}
		baseApiResponse = ResponseBuilder.resourceNotFound();
		log.debug("Get All Customers Method Ended");
		return new ResponseEntity<>(baseApiResponse, HttpStatus.NOT_FOUND);

	}

	@GetMapping("/getCustomer/{id}")
	public ResponseEntity<BaseApiResponse> viewCustomerProfile(HttpServletRequest request,
			@PathVariable @Min(1) Long id) {
		log.debug("viewCustomerProfile by Id Method Started");
		BaseApiResponse baseApiResponse = null;
		if (customerService.getCustomerById(id).isPresent()) {
			Optional<Customers> customerRequest = customerService.getCustomerById(id);

			baseApiResponse = ResponseBuilder.getSuccessResponse(customerRequest);
			return new ResponseEntity<>(baseApiResponse, HttpStatus.OK);
		}
		baseApiResponse = ResponseBuilder.resourceNotFound();
		log.debug("viewCustomerProfile by Id Method Ended");
		return new ResponseEntity<>(baseApiResponse, HttpStatus.NOT_FOUND);

	}

	@PostMapping("/createCustomer")
	public ResponseEntity<BaseApiResponse> addCustomer(@Valid @RequestBody CustomersRequest customerRequest,
			HttpServletRequest request) {
		log.debug("addCustomer Method Started");
		Customers customer = customerService.createCustomer(customerRequest);
		BaseApiResponse baseApiResponse = ResponseBuilder.getSuccessResponseForCreation(customer);
		log.debug("addCustomer Method Ended");
		return new ResponseEntity<>(baseApiResponse, HttpStatus.CREATED);

	}

	@PutMapping("/customer/{customerId}")
	public ResponseEntity<BaseApiResponse> updateCustomer(@PathVariable @Min(1) Long customerId,
			@Valid @RequestBody CustomersRequest customersRequest, HttpServletRequest request) {
		log.debug("updateCustomer Method Started");
		if (customerService.getCustomerById(customerId).isPresent()) {
			Optional<Customers> customer = customerService.getCustomerById(customerId)
					.map(customerDetail -> customerService.updateCustomer(customerId, customersRequest));
			BaseApiResponse baseApiResponse = ResponseBuilder.getSuccessResponse(customer);
			return new ResponseEntity<>(baseApiResponse, HttpStatus.OK);

		}
		BaseApiResponse baseApiResponse = ResponseBuilder.resourceNotFound();
		log.debug("updateCustomer Method Ended");
		return new ResponseEntity<>(baseApiResponse, HttpStatus.NOT_FOUND);
	}

}
