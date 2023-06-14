package com.jore.epoc.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jore.epoc.bo.step.DistributionStep;

public interface DistributionStepRepository extends CrudRepository<DistributionStep, Long> {
    List<DistributionStep> findByDistributionInMarketCompanyId(Long companyId);
}
