package com.eval.license.service;

import java.util.List;

import com.eval.license.model.EvalSkus;

public interface EvalSkusService {
	List<EvalSkus> findBySkuAndProductDescription(String sku, String productDescription);

}
