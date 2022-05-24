package com.eval.license.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import com.eval.license.model.Customers;
import com.eval.license.repository.CustomerRepository;
import com.eval.license.repository.EvalSkusRepository;
import com.eval.licenses.request.CustomersRequest;

@Transactional
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private EvalSkusRepository evalSkusRepository;

	@InjectMocks
	private CustomerServiceImpl customerService;

	@InjectMocks
	private EvalSkusServiceImpl evalSkusService;

	@Test
	void testFindAll() throws ParseException {
		CustomersRequest customerDetail = new CustomersRequest();
		customerDetail.setCustomerId(11L);
		customerDetail.setCustomerEmail("x@gmail.com");
		customerDetail.setCustomerNumber(1389);
		customerDetail.setRequestTraceId("asdasdasd");
		Customers customers1 = new Customers();
		customers1.setCustomerId(customerDetail.getCustomerId());
		customers1.setEmail(customerDetail.getCustomerEmail());
		customers1.setStoreCustomerId(customerDetail.getCustomerNumber());
		customers1.setEvalLicenseList(Arrays.asList());
		Customers customers2 = new Customers();
		customers2.setCustomerId(customerDetail.getCustomerId());
		customers2.setEmail(customerDetail.getCustomerEmail());
		customers2.setStoreCustomerId(customerDetail.getCustomerNumber());
		customers2.setEvalLicenseList(Arrays.asList());
		List<Customers> customers = new ArrayList<Customers>();
		customers.add(customers1);
		customers.add(customers2);
		Mockito.when(customerRepository.findAll()).thenReturn(customers);
		List<Customers> customer = customerService.findAll();
		assertEquals(11L, customer.get(0).getCustomerId());
		assertEquals(11L, customer.get(1).getCustomerId());

	}

	@Test
	void testGetCustomerById() throws ParseException {
		final Long customerId = 11L;
		CustomersRequest customerDetail = new CustomersRequest();
		customerDetail.setCustomerId(customerId);
		customerDetail.setCustomerEmail("x@gmail.com");
		customerDetail.setCustomerNumber(1389);
		customerDetail.setRequestTraceId("asdasdasd");
		Customers customers = new Customers();
		customers.setCustomerId(customerDetail.getCustomerId());
		customers.setEmail(customerDetail.getCustomerEmail());
		customers.setStoreCustomerId(customerDetail.getCustomerNumber());
		customers.setEvalLicenseList(Arrays.asList());
		Mockito.when(customerRepository.findById(12L)).thenReturn(Optional.of(customers));
		Optional<Customers> customer = customerService.getCustomerById(12L);
		assertEquals(11L, customer.get().getCustomerId());
	}

	

	@Test
	void testUpdateCustomer() throws ParseException {
		Long customerId = 11L;
		CustomersRequest customerDetail = new CustomersRequest();
		customerDetail.setCustomerId(11L);
		customerDetail.setCustomerEmail("x@gmail.com");
		customerDetail.setCustomerNumber(1389);
		customerDetail.setEvalLicenseList(Arrays.asList());
		Customers customers = new Customers();
		customers.setCustomerId(customerDetail.getCustomerId());
		customers.setEmail(customerDetail.getCustomerEmail());
		customers.setStoreCustomerId(customerDetail.getCustomerNumber());
		customers.setEvalLicenseList(customerDetail.getEvalLicenseList());
		Mockito.when(customerRepository.save(customers)).thenReturn(customers);
		Mockito.when(customerService.getCustomerById(customerId)).thenReturn(Optional.of(customers));
		Customers expected = customerService.updateCustomer(customerId, customerDetail);
		assertThat(expected).isNotNull();
		assertEquals(11L, customers.getCustomerId());
		verify(customerRepository).save(Mockito.any(Customers.class));

	}

	@Test
	void testshouldReturn404UpdateCustomer() throws ParseException {
		Long customerId = 11L;
		CustomersRequest customerDetail = new CustomersRequest();
		customerDetail.setCustomerId(11L);
		customerDetail.setCustomerEmail("x@gmail.com");
		customerDetail.setCustomerNumber(1389);
		customerDetail.setEvalLicenseList(Arrays.asList());
		Customers customers = new Customers();
		customers.setCustomerId(customerDetail.getCustomerId());
		customers.setEmail(customerDetail.getCustomerEmail());
		customers.setStoreCustomerId(customerDetail.getCustomerNumber());
		customers.setEvalLicenseList(customerDetail.getEvalLicenseList());
		Mockito.when(customerService.getCustomerById(customerId)).thenReturn(Optional.empty());
		Customers expected = customerService.updateCustomer(customerId, customerDetail);
		assertThat(expected).isNull();
	}

}
