package com.jore.epoc.repositories;

import org.springframework.data.repository.CrudRepository;

import com.jore.epoc.bo.Company;

public interface CompanyRepository extends CrudRepository<Company, Integer> {
}
