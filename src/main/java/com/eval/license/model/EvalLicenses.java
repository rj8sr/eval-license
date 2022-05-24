package com.eval.license.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "evallicenses")
public class EvalLicenses {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "serialkey")
	private String serialKey;

	@Column(name = "requestTraceId")
	private String requestTraceId;

	public String getRequestTraceId() {
		return requestTraceId;
	}

	public void setRequestTraceId(String requestTraceId) {
		this.requestTraceId = requestTraceId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	@Column(name = "creationdate", nullable = false, updatable = false)
	private Date creationDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "expirationdate")
	private Date expirationDate;

	@ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
	@JsonIgnoreProperties({ "licenses" })
	@JoinColumn(name = "skuId")
	private EvalSkus evalSkus;

	@ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "customerId")
	@JsonIgnoreProperties({ "order" })
	private Customers customers;

	public EvalSkus getEvalSkus() {
		return evalSkus;
	}

	public void setEvalSkus(EvalSkus evalSkus) {
		this.evalSkus = evalSkus;
	}

	public Customers getCustomers() {
		return customers;
	}

	public void setCustomers(Customers customers) {
		this.customers = customers;
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

	public EvalLicenses() {

	}

	public EvalLicenses(String serialKey, String requestTraceId, Date expirationDate, Customers customers,EvalSkus evalSkus
			) {
		super();
		this.serialKey = serialKey;
		this.requestTraceId = requestTraceId;
		this.expirationDate = expirationDate;
		this.evalSkus = evalSkus;
		this.customers = customers;
	}

	

}
