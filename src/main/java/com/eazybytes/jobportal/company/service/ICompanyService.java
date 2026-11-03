package com.eazybytes.jobportal.company.service;

import com.eazybytes.jobportal.dto.CompanyDto;
import jakarta.validation.Valid;

import java.util.List;

public interface ICompanyService {

    List<CompanyDto> getAllCompanies();

    boolean createCompany(CompanyDto companyDto);

    List<CompanyDto> getAllCompaniesForAdmin();

    boolean updateCompanyDetails(Long id, @Valid CompanyDto companyDto);

    void deleteCompanyById(Long id);
}
