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
@Table(name = "evalskus")
public class EvalSkus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "skuid", nullable = false)
	private Long skuId;

	@Column(name = "sku")
	private String sku;

	@Column(name = "productdescription")
	private String productDescription;

	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	@Column(name = "created", nullable = false, updatable = false)
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	@Column(name = "modified")
	private Date modifiedDate;

	@OneToMany(mappedBy = "evalSkus", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JsonIgnoreProperties({ "licenses" })
	private List<EvalLicenses> licenses;

	public List<EvalLicenses> getLicenses() {
		return licenses;
	}

	public void setLicenses(List<EvalLicenses> licenses) {
		this.licenses = licenses;
	}

	public Long getSkuId() {
		return skuId;
	}

	public void setSkuId(Long skuId) {
		this.skuId = skuId;
	}

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

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public EvalSkus() {
	}

	public EvalSkus(String sku, String productDescription) {
		super();
		this.sku = sku;
		this.productDescription = productDescription;
	}


}
