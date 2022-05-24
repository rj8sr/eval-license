package com.eval.license.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "customers")
public class Customers {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long customerId;

	@Column(name = "companyinfo")
	private String companyInfo;

	@Column(name = "email")
	private String email;

	@Column(name = "emailprofile")
	private String emailProfile;

	@Column(name = "employeecount")
	private Integer employeeCount;

	@Column(name = "initiatives")
	private String initiatives;

	@Column(name = "localprofile")
	private String localProfile;

	@Column(name = "optin")
	private Integer optin;

	@Column(name = "storecustomerid")
	private Integer storeCustomerId;

	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	@Column(name = "created", nullable = false, updatable = false)
	private Date created;

	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	@Column(name = "modified")
	private Date modified;

	@OneToMany(mappedBy = "customers", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JsonIgnoreProperties({ "customers" })
	private List<EvalLicenses> evalLicenseList;

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public String getCompanyInfo() {
		return companyInfo;
	}

	public void setCompanyInfo(String companyInfo) {
		this.companyInfo = companyInfo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmailProfile() {
		return emailProfile;
	}

	public void setEmailProfile(String emailProfile) {
		this.emailProfile = emailProfile;
	}

	public Integer getEmployeeCount() {
		return employeeCount;
	}

	public void setEmployeeCount(Integer employeeCount) {
		this.employeeCount = employeeCount;
	}

	public String getInitiatives() {
		return initiatives;
	}

	public void setInitiatives(String initiatives) {
		this.initiatives = initiatives;
	}

	public String getLocalProfile() {
		return localProfile;
	}

	public void setLocalProfile(String localProfile) {
		this.localProfile = localProfile;
	}

	public Integer getOptin() {
		return optin;
	}

	public void setOptin(Integer optin) {
		this.optin = optin;
	}

	public Integer getStoreCustomerId() {
		return storeCustomerId;
	}

	public void setStoreCustomerId(Integer storeCustomerId) {
		this.storeCustomerId = storeCustomerId;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public List<EvalLicenses> getEvalLicenseList() {
		return evalLicenseList;
	}

	public void setEvalLicenseList(List<EvalLicenses> evalLicenseList) {
		this.evalLicenseList = evalLicenseList;
	}

	}
