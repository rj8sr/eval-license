package com.eval.license.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eval.license.model.EvalLicenses;

@Repository
@Transactional
public interface EvalLicensesRepository extends JpaRepository<EvalLicenses, Long> {

}
