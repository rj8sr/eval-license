package com.eval.license.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import com.eval.license.model.Customers;
import com.eval.license.model.EvalLicenses;
import com.eval.license.model.EvalSkus;
import com.eval.license.repository.CustomerRepository;
import com.eval.license.service.CustomerService;
import com.eval.licenses.request.CustomersRequest;
import com.eval.licenses.request.EvalLicensesRequest;

@Transactional
@Service
public class CustomerServiceImpl implements CustomerService {

	private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	EvalSkusServiceImpl evalSkusService;

	@Override
	public Customers createCustomer(@Valid CustomersRequest customerRequest) {
		log.debug("createCustomer Method In CustomerService Started");
		Customers customer = new Customers();
		List<EvalLicenses> evalLicensesDetail = new ArrayList<>();
		List<EvalLicensesRequest> evalLicenses = customerRequest.getOrder().getLicenses();
		for (EvalLicensesRequest evalLicensesRequest : evalLicenses) {
			List<EvalSkus> evalSkusDetail = evalSkusService.findBySkuAndProductDescription(
					evalLicensesRequest.getSku(), evalLicensesRequest.getProductDescription());
			if (evalSkusDetail.size() > 0) {
				if (evalSkusDetail.get(0).getSku().equals(evalLicensesRequest.getSku()) && evalSkusDetail.get(0)
						.getProductDescription().equals(evalLicensesRequest.getProductDescription())) {
					log.debug("");
				} else {
					evalLicensesDetail.add(new EvalLicenses(evalLicensesRequest.getSerialKey(),
							customerRequest.getRequestTraceId(), evalLicensesRequest.getExpirationDate(), customer,
							new EvalSkus(evalLicensesRequest.getSku(), evalLicensesRequest.getProductDescription())));
				}
			} else {
				evalLicensesDetail.add(new EvalLicenses(evalLicensesRequest.getSerialKey(),
						customerRequest.getRequestTraceId(), evalLicensesRequest.getExpirationDate(), customer,
						new EvalSkus(evalLicensesRequest.getSku(), evalLicensesRequest.getProductDescription())));
			}
		}
		customer.setEvalLicenseList(evalLicensesDetail);
		customer.setEmail(customerRequest.getCustomerEmail());		
		customer.setStoreCustomerId(customerRequest.getCustomerNumber());
		log.debug("createCustomer Method In CustomerService Ended");
		return customerRepository.save(customer);
	}

	@Override
	public List<Customers> findAll() {
		log.debug("Inside findAll Method In CustomerService");
		return customerRepository.findAll();
	}

	@Override
	public Optional<Customers> getCustomerById(Long id) {
		log.debug("Inside getCustomerById Method In CustomerService");
		return customerRepository.findById(id);
	}

	@Override
	public Customers updateCustomer(@PathVariable @Min(1) Long customerId, @Valid CustomersRequest customerRequest) {
		log.debug("updateCustomer Method In CustomerService Started");
		Optional<Customers> customers = customerRepository.findById(customerId);
		if (customers.isPresent()) {
			Customers customer = customers.get();
			customer.setEmail(customerRequest.getCustomerEmail());
			customer.setStoreCustomerId(customerRequest.getCustomerNumber());

			return customerRepository.save(customer);
		}
		log.debug("updateCustomer Method In CustomerService Ended");
		return null;

	}

}