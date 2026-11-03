package com.eazybytes.jobportal.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record CompanyDto(

                         Long id,

                         @NotBlank(message = "Name cannot be empty")
                         String name,

                         @NotBlank(message = "Logo cannot be empty")
                         String logo,

                         @NotBlank(message = "Industry cannot be empty")
                         String industry,

                         @NotBlank(message = "Size cannot be empty")
                         String size,

                         @DecimalMin(value="0.0", message = "Rating must be at least 0")
                         @DecimalMax(value="5.0", message = "Rating must be at most 5")
                         BigDecimal rating,

                         @NotBlank(message = "Locations cannot be empty")
                         String locations,

                         @Min(value = 1900, message = "Founded year must be 1900 or greater")
                         Integer founded,

                         @NotBlank(message = "Description cannot be empty")
                         String  description,

                         @Min(value = 1, message = "Employees  must be  greater than or equal to 1")
                         Integer  employees,

                         @NotBlank(message = "Website cannot be empty")
                         String  website,

                         Instant createdAt,

                         List<JobDto> jobs

) {


}
