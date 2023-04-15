package com.jore.epoc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.jore.epoc.bo.Company;
import com.jore.epoc.dto.CompanyDto;

@Mapper
public interface CompanyMapper {
    public CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);

    public Company companyDtoToCompany(CompanyDto companyDto);

    public CompanyDto companyToCompanyDto(Company company);
}
