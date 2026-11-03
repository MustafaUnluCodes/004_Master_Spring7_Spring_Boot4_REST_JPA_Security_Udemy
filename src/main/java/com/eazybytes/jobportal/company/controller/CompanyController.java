package com.eazybytes.jobportal.company.controller;

import com.eazybytes.jobportal.aspects.LogAspect;
import com.eazybytes.jobportal.company.service.ICompanyService;
import com.eazybytes.jobportal.dto.CompanyDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping(path = "/companies")
@RequiredArgsConstructor
//@CrossOrigin({"http://localhost:5173"})
public class CompanyController {

    private final ICompanyService companyService;

    /*

    @Autowired  // Optional since there's a single constructor.
    public CompanyController(ICompanyService companyService) {
        this.companyService = companyService;
    }

     */

//    @LogAspect
    @GetMapping(path="/public", version = "1.0")
    public ResponseEntity<List<CompanyDto>> getAllCompanies() {

        List<CompanyDto> companyList = companyService.getAllCompanies();

        return ResponseEntity.ok().body(companyList);


    }

    @PostMapping(path="/admin", version = "1.0")
    public ResponseEntity<String> addCompany(@RequestBody @Valid CompanyDto companyDto) {

        boolean isCreated = companyService.createCompany(companyDto);

        if  (isCreated) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Company created successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Request processing failed.");
        }


    }


    @PutMapping(path="/{id}/admin", version = "1.0")
    public ResponseEntity<String> updateCompany(
            @PathVariable
            @Validated
            @NotBlank(message = "Company ID cannot be blank")
            String id,
            @RequestBody @Valid CompanyDto companyDto) {

        boolean isUpdated = companyService.updateCompanyDetails(Long.valueOf(id), companyDto);

        if (isUpdated) {
            return ResponseEntity.status(HttpStatus.OK).body("Company details updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update company details.");
        }

    }

    @DeleteMapping  (path="/{id}/admin", version = "1.0")
    public ResponseEntity<String> deleteCompany(
            @PathVariable
            @Validated
            @NotBlank(message = "Company ID cannot be blank")
            String id) {

        companyService.deleteCompanyById(Long.valueOf(id));
        return ResponseEntity.status(HttpStatus.OK).body("Company record deleted successfully");
    }

    @GetMapping(path="/admin", version = "1.0")
    public ResponseEntity<List<CompanyDto>> getAllCompaniesForAdmin() {

        List<CompanyDto> companyList = companyService.getAllCompaniesForAdmin();

        return ResponseEntity.ok().body(companyList);

    }




}
