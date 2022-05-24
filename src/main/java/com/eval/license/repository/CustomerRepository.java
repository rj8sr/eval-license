package com.eval.license.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eval.license.model.Customers;

@Repository
@Transactional
public interface CustomerRepository extends JpaRepository<Customers, Long> {

}
