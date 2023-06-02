package com.jore.epoc.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.jore.epoc.bo.Company;
import com.jore.epoc.dto.CompanyDto;

public interface CompanyMapper {
    CompanyMapper INSTANCE = new CompanyMapper() {
        @Override
        public CompanyDto companyToCompanyDto(Company company) {
            CompanyDto result = CompanyDto.builder().id(company.getId()).name(company.getName()).simulationId(company.getSimulation() != null ? company.getSimulation().getId() : null).users(UserMapper.INSTANCE.userToUserDto(company.getUsers())).build();
            company.getUsers().forEach(userRole -> result.addUserEmail(userRole.getUser().getEmail()));
            return result;
        }

        @Override
        public List<CompanyDto> companyToCompanyDto(List<Company> companies) {
            return companies.stream().map(company -> companyToCompanyDto(company)).collect(Collectors.toList());
        }
    };

    CompanyDto companyToCompanyDto(Company company);

    List<CompanyDto> companyToCompanyDto(List<Company> companies);
}
