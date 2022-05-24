package com.eval.license.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.eval.license.model.Customers;
import com.eval.license.model.EvalLicenses;
import com.eval.license.model.EvalSkus;
import com.eval.licenses.request.CustomersRequest;
import com.eval.licenses.request.EvalLicensesRequest;
import com.eval.licenses.request.OrderRequest;

@DataJpaTest
@ActiveProfiles("dev")
class CustomerRepositoryTest {

	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	EvalSkusRepository evalSkusRepository;

	private List<Customers> customers;
	
	@BeforeEach
	  void setUp() throws ParseException {
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
		customers2.setCustomerId(12L);
		customers2.setEmail(customerDetail.getCustomerEmail());
		customers2.setStoreCustomerId(customerDetail.getCustomerNumber());
		customers2.setEvalLicenseList(Arrays.asList());
		customers = new ArrayList<Customers>();
		customers.add(customers1);
		customers.add(customers2);
		customerRepository.saveAll(customers);
	}
	  @AfterEach
	    public void destroyAll(){
	        customerRepository.deleteAll();
	    }

	@Test
	public void testSaveCustomer() throws ParseException {
		CustomersRequest customerRequest = new CustomersRequest();
		EvalLicensesRequest evalLicenseRequest1 = new EvalLicensesRequest();
		EvalLicensesRequest evalLicenseRequest2 = new EvalLicensesRequest();
		EvalSkus evalSkus = new EvalSkus();
		OrderRequest orderRequest = new OrderRequest();
		customerRequest.setCustomerId(11L);
		customerRequest.setCustomerEmail("gmail@gmail.com");
		customerRequest.setCustomerNumber(1389);
		customerRequest.setRequestTraceId("XYZ-SDSF-SDS");
		Customers customer = new Customers();
		customer.setCustomerId(customerRequest.getCustomerId());
		customer.setEmail(customerRequest.getCustomerEmail());
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
			List<EvalSkus> evalSkusDetail = evalSkusRepository.findBySkuAndProductDescription(
					evalLicensesRequest.getSku(), evalLicensesRequest.getProductDescription());
			if (evalSkusDetail.size() > 0) {
				if (evalSkusDetail.get(0).getSku().equals(evalLicensesRequest.getSku()) && evalSkusDetail.get(0)
						.getProductDescription().equals(evalLicensesRequest.getProductDescription())) {
				} else {
					evalSkus.setSkuId(18L);

					evalLicenseRequest1.setCustomer(customer);
					evalLicenseRequest1.setEvalSkus(
							new EvalSkus(evalLicensesRequest.getSku(), evalLicensesRequest.getProductDescription()));
					evalLicenseRequest2.setCustomer(customer);
					evalLicenseRequest2.setEvalSkus(
							new EvalSkus(evalLicensesRequest.getSku(), evalLicensesRequest.getProductDescription()));
					evalLicensesDetail.add(new EvalLicenses(evalLicensesRequest.getSerialKey(),
							customerRequest.getRequestTraceId(), evalLicensesRequest.getExpirationDate(),
							new Customers(),
							new EvalSkus(evalLicensesRequest.getSku(), evalLicensesRequest.getProductDescription())));
				}

			} else {
				evalSkus.setSkuId(18L);

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
		}

		customer.setEvalLicenseList(evalLicensesDetail);
		customerRepository.save(customer);
		assertNotNull(customer);
		assertEquals(11L, customer.getCustomerId());
	}

	@Test
	public void testfindAllCustomers() throws ParseException {
		List<Customers> customer = customerRepository.findAll();
		assertEquals(2, customer.size());
	}

	
}
