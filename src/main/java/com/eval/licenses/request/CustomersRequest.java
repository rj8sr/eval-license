package com.eval.licenses.request;

import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import com.eval.license.model.EvalLicenses;

public class CustomersRequest {

	@Min(value = 1, message = "Id can't be less than 1 or bigger than 999999")
	@Max(999999)
	private Long customerId;

	@Min(value = 1, message = "storeCustomerId can't be less than 1 or bigger than 10000")
	@Max(1000000000)
	private Integer customerNumber;

	@Size(max = 254)
	@Email(message = "Email is not valid")
	//@NotBlank(message = "Email should not be blank")
	private String customerEmail;

	@Size(max = 50)
	//@NotBlank(message = "Request Id  should not be blank")
	private String requestTraceId;

	private OrderRequest order;

	private List<EvalLicenses> evalLicenseList;

	public List<EvalLicenses> getEvalLicenseList() {
		return evalLicenseList;
	}

	public void setEvalLicenseList(List<EvalLicenses> evalLicenseList) {
		this.evalLicenseList = evalLicenseList;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Integer getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(Integer customerNumber) {
		this.customerNumber = customerNumber;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getRequestTraceId() {
		return requestTraceId;
	}

	public void setRequestTraceId(String requestTraceId) {
		this.requestTraceId = requestTraceId;
	}

	public OrderRequest getOrder() {
		return order;
	}

	public void setOrder(OrderRequest order) {
		this.order = order;
	}

}
