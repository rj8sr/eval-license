package com.eval.licenses.request;

import java.util.List;

public class OrderRequest {
	private List<EvalLicensesRequest> licenses;

	public List<EvalLicensesRequest> getLicenses() {
		return licenses;
	}

	public void setLicenses(List<EvalLicensesRequest> licenses) {
		this.licenses = licenses;
	}

	
}
