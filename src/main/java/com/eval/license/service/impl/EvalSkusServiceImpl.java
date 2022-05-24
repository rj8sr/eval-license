package com.eval.license.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eval.license.model.EvalSkus;
import com.eval.license.repository.EvalSkusRepository;
import com.eval.license.service.EvalSkusService;

@Service
@Transactional
public class EvalSkusServiceImpl implements EvalSkusService {

	@Autowired
	EvalSkusRepository evalSkusRepository;

	@Override
	public List<EvalSkus> findBySkuAndProductDescription(String sku, String productDescription) {
		return evalSkusRepository.findBySkuAndProductDescription(sku, productDescription);
	}


	
}