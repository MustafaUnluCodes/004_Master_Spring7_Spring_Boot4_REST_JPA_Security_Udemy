package com.eazybytes.jobportal.repository;


import com.eazybytes.jobportal.entity.Company;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;


@Repository // mentioning this annotation is completely 'Optional'.
public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query("SELECT DISTINCT c FROM Company c  JOIN FETCH c.jobs j WHERE j.status = :status")
    List<Company> findAllWithJobsByStatus(@Param("status") String status);

    @Cacheable("jobs")
    List<Company> fetchCompaniesWithJobsByStatus(@Param("status") String status);

    @Query(value="SELECT DISTINCT c.* FROM companies c  JOIN  jobs j  on c.id=j.company_id WHERE j.status = :status"
            , nativeQuery = true)
//    @Query("SELECT DISTINCT c.* FROM companies c  JOIN  jobs j  on c.id=j.company_id WHERE j.status = ?1") // Alternative 1:  positional parameters
//    @Query("SELECT DISTINCT c.* FROM companies c  JOIN  jobs j  on c.id=j.company_id WHERE j.status = ?") // Alternative 1.1: positional parameters mentioning the position is optional since there's only one parameter
    List<Company> findAllWithJobsByStatusNative(@Param("status") String status);

    List<Company> fetchCompaniesWithJobsByStatusNative(@Param("status") String status);

    @CacheEvict(value = "companies", allEntries = true)
    void deleteById(Long id);

    @CacheEvict(value = "companies", allEntries = true)
    Company save(Company entity);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @CacheEvict(value = "companies", allEntries = true)
    int updateCompanyDetails(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("logo") String logo,
            @Param("industry") String industry,
            @Param("size") String size,
            @Param("rating") BigDecimal rating,
            @Param("locations") String locations,
            @Param("founded") Integer founded,
            @Param("description") String description,
            @Param("employees") Integer employees,
            @Param("website") String website
    );

}
