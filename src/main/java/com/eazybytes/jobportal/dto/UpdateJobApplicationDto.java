package com.eazybytes.jobportal.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateJobApplicationDto(

        @NotNull(message = "Application ID is required")
        JobApplicationStatus status,

        @NotNull(message = "Status is required")
        Long applicationId,

        String notes
) {
}
