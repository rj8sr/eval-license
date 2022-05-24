package com.eval.licenses.request;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import com.eval.license.model.Customers;
import com.eval.license.model.EvalSkus;

public class EvalLicensesRequest {

	@Min(value = 1, message = "Id can't be less than 1 or bigger than 999999")
	@Max(999999)
	private Long id;

	@Size(max = 200)
	@NotBlank(message = "Enter the serialKey")
	private String serialKey;

	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	private Date creationDate;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "Please provide a date.")
	private Date expirationDate;

	@NotBlank(message = "Enter the sku value")
	@Size(max = 100)	
	private String sku;

	@Size(max = 500, message = "productDescription should not exceed 500 characters")
	@NotBlank(message = "Enter the product description")
	private String productDescription;

	private EvalSkus evalSkus;

	private Customers customer;

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public Customers getCustomer() {
		return customer;
	}

	public void setCustomer(Customers customer) {
		this.customer = customer;
	}

	public EvalSkus getEvalSkus() {
		return evalSkus;
	}

	public void setEvalSkus(EvalSkus evalSkus) {
		this.evalSkus = evalSkus;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSerialKey() {
		return serialKey;
	}

	public void setSerialKey(String serialKey) {
		this.serialKey = serialKey;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

}
