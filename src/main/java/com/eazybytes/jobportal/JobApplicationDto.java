package com.eazybytes.jobportal;

import com.eazybytes.jobportal.dto.JobDto;
import com.eazybytes.jobportal.dto.ProfileDto;
import com.eazybytes.jobportal.entity.JobPortalUser;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;


public record JobApplicationDto(
        Long id,
        Long userId,
        String userName,
        String userEmail,
        String userMobileNumber,
        ProfileDto userProfile,
        JobDto job,
        Instant appliedAt,
        String status,
        String coverLetter,
        String notes

)  {
}