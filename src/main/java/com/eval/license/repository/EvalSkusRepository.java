package com.eval.license.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eval.license.model.EvalSkus;

@Repository
@Transactional
public interface EvalSkusRepository extends JpaRepository<EvalSkus, Long> {

	List<EvalSkus> findBySkuAndProductDescription(String sku, String productDescription);

}