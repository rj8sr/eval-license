package com.eval.license.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.eval.license.model.Customers;
import com.eval.license.model.EvalLicenses;
import com.eval.license.model.EvalSkus;
import com.eval.license.service.impl.CustomerServiceImpl;
import com.eval.license.service.impl.EvalSkusServiceImpl;
import com.eval.licenses.request.CustomersRequest;
import com.eval.licenses.request.EvalLicensesRequest;
import com.eval.licenses.request.OrderRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = EvalController.class)
class EvalControllerTest {

	@MockBean
	CustomerServiceImpl customerService;

	@MockBean
	EvalSkusServiceImpl evalSkusService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void testGetAllCustomers() throws Exception {
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
		Mockito.when(customerService.findAll()).thenReturn(customers);
		this.mockMvc.perform(get("/api/getAllCustomers")).andExpect(status().isOk())
				.andExpect(jsonPath("$.responseStatus.statusCode", is(200)))
				.andExpect(jsonPath("$.responseData.size()", is(customers.size())));
	}

	@Test
	void testshouldReturn404WhenGetAllCustomers() throws Exception {
		Mockito.when(customerService.findAll()).thenReturn(Arrays.asList());
		this.mockMvc.perform(get("/api/getAllCustomers")).andExpect(jsonPath("$.responseStatus.statusCode", is(404)));
	}

	@Test
	void testViewCustomerProfile() throws Exception {
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
		Mockito.when(customerService.getCustomerById(customerId)).thenReturn(Optional.of(customers));
		this.mockMvc.perform(get("/api/getCustomer/{id}", customerId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.responseStatus.statusCode", is(200)))
				.andExpect(jsonPath("$.responseData.email", is(customers.getEmail())));
	}

	@Test
	void testshouldReturn404WhenViewCustomerProfile() throws Exception {
		final Long customerId = 11L;
		Mockito.when(customerService.getCustomerById(customerId)).thenReturn(Optional.empty());
		this.mockMvc.perform(get("/api/getCustomer/{id}", customerId))
				.andExpect(jsonPath("$.responseStatus.statusCode", is(404)));
	}

	@Test
	void testAddCustomer() throws JsonProcessingException, Exception {
		CustomersRequest customerRequest = new CustomersRequest();
		EvalLicensesRequest evalLicenseRequest1 = new EvalLicensesRequest();
		EvalLicensesRequest evalLicenseRequest2 = new EvalLicensesRequest();
		OrderRequest orderRequest = new OrderRequest();
		customerRequest.setCustomerId(11L);
		customerRequest.setCustomerEmail("gmail@gmail.com");
		customerRequest.setCustomerNumber(1389);
		customerRequest.setRequestTraceId("XYZ-SDSF-SDS");
		Customers customer = new Customers();
		customer.setCustomerId(customerRequest.getCustomerId());
		customer.setEmail(customerRequest.getCustomerEmail());
		customer.setCompanyInfo(null);
		customer.setEmailProfile(null);
		customer.setEmployeeCount(null);
		customer.setInitiatives(null);
		customer.setLocalProfile(null);
		customer.setOptin(null);
		customer.setCreated(null);
		customer.setModified(null);
		customer.setStoreCustomerId(customerRequest.getCustomerNumber());
		List<EvalLicensesRequest> evalLicense = new ArrayList<EvalLicensesRequest>();
		evalLicenseRequest1.setId(1L);
		evalLicenseRequest1.setSku("erwe1");
		evalLicenseRequest1.setProductDescription("ewewer1");
		evalLicenseRequest1.setSerialKey("ewefwef1");
		evalLicenseRequest1.setExpirationDate(new SimpleDateFormat("yyyy-MM-dd").parse("2021-05-24"));
		evalLicenseRequest2.setId(2L);
		evalLicenseRequest2.setSku("erwe2");
		evalLicenseRequest2.setProductDescription("ewewer2");
		evalLicenseRequest2.setSerialKey("ewefwef2");
		evalLicenseRequest2.setExpirationDate(new SimpleDateFormat("yyyy-MM-dd").parse("2021-05-29"));
		evalLicense.add(evalLicenseRequest1);
		evalLicense.add(evalLicenseRequest2);
		orderRequest.setLicenses(evalLicense);
		customerRequest.setOrder(orderRequest);
		List<EvalLicenses> evalLicensesDetail = new ArrayList<EvalLicenses>();
		List<EvalLicensesRequest> evalLicenses = customerRequest.getOrder().getLicenses();
		for (EvalLicensesRequest evalLicensesRequest : evalLicenses) {
			evalLicenseRequest1.setCustomer(customer);
			evalLicenseRequest1.setEvalSkus(
					new EvalSkus(evalLicensesRequest.getSku(), evalLicensesRequest.getProductDescription()));
			evalLicenseRequest2.setCustomer(customer);
			evalLicenseRequest2.setEvalSkus(
					new EvalSkus(evalLicensesRequest.getSku(), evalLicensesRequest.getProductDescription()));
			evalLicensesDetail.add(new EvalLicenses(evalLicensesRequest.getSerialKey(),
					customerRequest.getRequestTraceId(), evalLicensesRequest.getExpirationDate(), new Customers(),
					new EvalSkus(evalLicensesRequest.getSku(), evalLicensesRequest.getProductDescription())));
		}
		customer.setEvalLicenseList(evalLicensesDetail);
		Mockito.when(customerService.createCustomer(Mockito.any(CustomersRequest.class))).thenReturn(customer);
		this.mockMvc
				.perform(post("/api/createCustomer").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(customer)))
				.andExpect(jsonPath("$.responseStatus.statusCode", is(201))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.responseData.email", is(customer.getEmail())));

	}

	
	@Test
	void testUpdateCustomer() throws JsonProcessingException, Exception {
		Long customerId = 11L;
		CustomersRequest customerDetail = new CustomersRequest();
		customerDetail.setCustomerId(11L);
		customerDetail.setCustomerEmail("x@gmail.com");
		customerDetail.setCustomerNumber(1389);
		customerDetail.setRequestTraceId("dfasdasd");
		Customers customers = new Customers();
		customers.setCustomerId(customerDetail.getCustomerId());
		customers.setEmail(customerDetail.getCustomerEmail());
		customers.setStoreCustomerId(customerDetail.getCustomerNumber());
		Mockito.when(customerService.getCustomerById(customerId)).thenReturn(Optional.of(customers));
		Mockito.when(customerService.updateCustomer(anyLong(), any(CustomersRequest.class))).thenReturn(customers);
		this.mockMvc.perform(put("/api/customer/{customerId}", customerDetail.getCustomerId())
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(customerDetail)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.responseStatus.statusCode", is(200)));

	}

	@Test
	void testshouldReturn404WhenUpdateCustomer() throws Exception {
		Long customerId = 11L;
		CustomersRequest customerDetail = new CustomersRequest();
		customerDetail.setCustomerId(11L);
		customerDetail.setCustomerEmail("x@gmail.com");
		customerDetail.setCustomerNumber(1389);
		customerDetail.setRequestTraceId("dfasdasd");
		Customers customers = new Customers();
		customers.setCustomerId(customerDetail.getCustomerId());
		customers.setEmail(customerDetail.getCustomerEmail());
		customers.setStoreCustomerId(customerDetail.getCustomerNumber());
		Mockito.when(customerService.getCustomerById(customerId)).thenReturn(Optional.empty());
		Mockito.when(customerService.updateCustomer(anyLong(), any(CustomersRequest.class))).thenReturn(customers);
		this.mockMvc.perform(put("/api/customer/{customerId}", customerDetail.getCustomerId())
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(customerDetail)))
				.andExpect(jsonPath("$.responseStatus.statusCode", is(404)));
	}

}
