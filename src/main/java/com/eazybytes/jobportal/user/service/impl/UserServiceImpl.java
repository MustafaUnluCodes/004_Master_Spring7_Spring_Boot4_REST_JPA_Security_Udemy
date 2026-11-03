package com.eazybytes.jobportal.user.service.impl;

import com.eazybytes.jobportal.JobApplicationDto;
import com.eazybytes.jobportal.constants.ApplicationConstants;
import com.eazybytes.jobportal.dto.ApplyJobRequestDto;
import com.eazybytes.jobportal.dto.JobDto;
import com.eazybytes.jobportal.dto.ProfileDto;
import com.eazybytes.jobportal.dto.UserDto;
import com.eazybytes.jobportal.entity.*;
import com.eazybytes.jobportal.repository.*;
import com.eazybytes.jobportal.user.service.IUserService;
import com.eazybytes.jobportal.util.ApplicationUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements IUserService {

    private final JobPortalUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;
    private final ProfileRepository profileRepository;
    private final JobRepository  jobRepository;
    private final JobApplicationRepository  jobApplicationRepository;

    @Override
    public Optional<UserDto> searchUserByEmail(String email) {

        return userRepository.findJobPortalUserByEmail(email)
                .map(this::mapToUserDto);

    }

    @Override
    @Transactional
    public UserDto elevateToEmployer(Long userId) {

        JobPortalUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (ApplicationConstants.ROLE_EMPLOYER.equals(user.getRole().getName())) {
                return mapToUserDto(user);
        }

        if (ApplicationConstants.ROLE_ADMIN.equals(user.getRole().getName())) {
             throw new RuntimeException("Cannot elevate admin user to employer role");
        }

        Role employerRole  = roleRepository.findRoleByName(ApplicationConstants.ROLE_EMPLOYER)
                .orElseThrow(() -> new RuntimeException("ROLE_EMPLOYER not found"));

        user.setRole(employerRole );
        // JobPortalUser updatedUser = userRepository.save(user);
        /**
         * Why it’s unnecessary
         * findById() returns a managed entity
         * You modify it inside a transaction
         * Dirty checking automatically updates it
         */


        return mapToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto assignCompanyToEmployer(Long userId, Long companyId) {

        JobPortalUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if  (!ApplicationConstants.ROLE_EMPLOYER.equals(user.getRole().getName())) {
            throw new RuntimeException("User must be an employer to be assigned to a company");
        }

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + companyId));

        user.setCompany(company);
        // JobPortalUser updatedUser = userRepository.save(user);  // optional, not needed due to dirty checking
        return mapToUserDto(user);
    }

    @Override
    @Transactional
    public ProfileDto createOrUpdateProfile(String userEmail, String profileJson,
                                            MultipartFile profilePicture, MultipartFile resume) throws JsonProcessingException {

        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("user not found with email: " + userEmail));

        Profile profile = user.getProfile();

        if (profile == null) {
            profile = new Profile();
            profile.setUser(user);
        }

        ObjectMapper objectMapper = new ObjectMapper();

        ProfileDto profileDto = objectMapper.readValue(profileJson, ProfileDto.class);

        Profile savedProfile = profileRepository.save(mapToProfile(profile, profileDto, profilePicture, resume));
        return maptoProfileDto(savedProfile, false);
    }

    @Override
    public ProfileDto getProfile(String userEmail) {

        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("user not found with email: " + userEmail));

        if  (user.getProfile()==null) {
            return null;
        }
        return maptoProfileDto(user.getProfile(), false);
    }

    @Override
    public ProfileDto getProfilePicture(String userEmail) {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("user not found with email: " + userEmail));

        if  (user.getProfile()==null) {
            return null;
        }
        return maptoProfileDto(user.getProfile(), true);
    }

    @Override
    public ProfileDto getResume(String userEmail) {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("user not found with email: " + userEmail));

        if  (user.getProfile()==null) {
            return null;
        }
        return maptoProfileDto(user.getProfile(), true);
    }

    @Override
    @Transactional
    public JobDto saveJob(String userEmail, Long jobId) {

        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("user not found with email: " + userEmail));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with jobId: " + jobId));

        user.getSavedJobs().add(job);

        //userRepository.save(user);  // no nedd to call, Hibernate will handle it.

        return ApplicationUtility.transformJobToDto(job);

    }

    @Override
    @Transactional
    public void unsaveJob(String userEmail, Long jobId) {


        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("user not found with email: " + userEmail));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with jobId: " + jobId));

        user.getSavedJobs().remove(job);

        //userRepository.save(user);  // no nedd to call, Hibernate will handle it.

    }

    @Override
    public List<JobDto> getSavedJobs(String userEmail) {

        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("user not found with email: " + userEmail));

       return user.getSavedJobs()
               .stream()
               .map(job -> ApplicationUtility.transformJobToDto(job))
               .collect(Collectors.toList());


    }

    @Override
    @Transactional
    public JobApplicationDto applyForJob(String userEmail, ApplyJobRequestDto applyJobRequestDto) {

        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("user not found with email: " + userEmail));

        Long jobId = applyJobRequestDto.jobId();

        if (jobApplicationRepository.existsByUserIdAndJobId(user.getId(), jobId)) {
            throw new RuntimeException("You have already applied for this job");
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with jobId: " + jobId));

        JobApplication application = new JobApplication();
        application.setUser(user);
        application.setJob(job);
        application.setAppliedAt(Instant.now());
        application.setStatus(ApplicationConstants.PENDING);
        application.setCoverLetter(applyJobRequestDto.coverLetter());

        JobApplication saved = jobApplicationRepository.save(application);

        job.setApplicationsCount(job.getApplicationsCount()  != null ?  job.getApplicationsCount() + 1 : 1);
        // jobRepository.save(job); - Optional
        return ApplicationUtility.mapToJobApplicationDto(saved);

    }

    @Override
    @Transactional
    public void withdrawApplication(String userEmail, Long jobId) {

        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("user not found with email: " + userEmail));


        if (!jobApplicationRepository.existsByUserIdAndJobId(user.getId(), jobId)) {
            throw new RuntimeException("You have not applied for this job");
        }

        jobApplicationRepository.deleteByUserIdAndJobId(user.getId(), jobId);

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));

        if (job.getApplicationsCount()  != null && job.getApplicationsCount()> 0) {
            job.setApplicationsCount(job.getApplicationsCount() - 1);
//            jobRepository.save(job);   -Optional
        }





    }

    @Override
    public List<JobApplicationDto> getJobSeekerApplications(String userEmail) {

        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("user not found with email: " + userEmail));

        return user.getJobApplications()
                .stream()
                .map(ApplicationUtility::mapToJobApplicationDto)
                .collect(Collectors.toList());


    }



    private ProfileDto maptoProfileDto(Profile profile, boolean includeBinaryData) {

        ProfileDto dto;

        if (includeBinaryData) {
            dto = new ProfileDto(profile.getId(), profile.getUser().getId(),
                    profile.getJobTitle(), profile.getLocation(), profile.getExperienceLevel(),
                    profile.getProfessionalBio(), profile.getPortfolioWebsite(), profile.getProfilePicture(),
                    profile.getProfilePictureName(), profile.getProfilePictureType(), profile.getResume(),
                    profile.getResumeName(), profile.getResumeType(), profile.getCreatedAt(), profile.getUpdatedAt()
            );
        } else {
            dto = new ProfileDto(profile.getId(), profile.getUser().getId(),
                    profile.getJobTitle(), profile.getLocation(), profile.getExperienceLevel(),
                    profile.getProfessionalBio(), profile.getPortfolioWebsite(), null,
                    profile.getProfilePictureName(), profile.getProfilePictureType(), null,
                    profile.getResumeName(), profile.getResumeType(), profile.getCreatedAt(), profile.getUpdatedAt());
        }
        return dto;

    }

    private Profile mapToProfile(Profile profile, ProfileDto profileDto,
                                 MultipartFile profilePicture, MultipartFile resume) {

        // Update text fields
        profile.setJobTitle(profileDto.jobTitle());
        profile.setLocation(profileDto.location());
        profile.setExperienceLevel(profileDto.experienceLevel());
        profile.setProfessionalBio(profileDto.professionalBio());
        profile.setPortfolioWebsite(profileDto.portfolioWebsite());

        // Handle profile picture upload
        if (profilePicture !=null && !profilePicture.isEmpty()) {
            try {
                profile.setProfilePicture(profilePicture.getBytes());
                profile.setProfilePictureName(profilePicture.getOriginalFilename());
                profile.setProfilePictureType(profilePicture.getContentType());
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload profile picture", e);
            }
        }

        // Handle resume upload
        if (resume !=null && !resume.isEmpty()) {
            try {
                profile.setResume(resume.getBytes());
                profile.setResumeName(resume.getOriginalFilename());
                profile.setResumeType(resume.getContentType());
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload resume", e);
            }
        }


        return profile;
    }

    private UserDto mapToUserDto(JobPortalUser user) {

        UserDto dto = new UserDto();

        BeanUtils.copyProperties(user, dto);

        dto.setUserId(user.getId());
        dto.setRole(user.getRole() != null ? user.getRole().getName() : null);
        dto.setCompanyId(user.getCompany() != null ? user.getCompany().getId() : null);
        dto.setCompanyName(user.getCompany() != null ? user.getCompany().getName() : null);

        return dto;


    }
}
