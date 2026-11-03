package com.eazybytes.jobportal.user.controller;

import com.eazybytes.jobportal.JobApplicationDto;
import com.eazybytes.jobportal.dto.ApplyJobRequestDto;
import com.eazybytes.jobportal.dto.JobDto;
import com.eazybytes.jobportal.dto.ProfileDto;
import com.eazybytes.jobportal.dto.UserDto;
import com.eazybytes.jobportal.entity.JobApplication;
import com.eazybytes.jobportal.user.service.IUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;


    @GetMapping(path = "/search/admin", version="1.0")
    public ResponseEntity<?> searchUserByEmail(@RequestParam String email) {

        Optional<UserDto> userOptional = userService.searchUserByEmail(email);

        if (userOptional.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found with email:" + email));
        }
        return ResponseEntity.ok(userOptional.get());

    }

    @PatchMapping(path = "/{userId}/role/employer/admin", version="1.0")
    public ResponseEntity<?> elevateToEmployer(@PathVariable Long userId) {
        UserDto updatedUser = userService.elevateToEmployer(userId);
        return ResponseEntity.ok(updatedUser);
    }

     @PatchMapping(path = "/{userId}/company/{companyId}/admin", version="1.0")
    public ResponseEntity<?> assignCompanyToEmployer
             (@PathVariable Long userId,
              @PathVariable Long companyId) {

         UserDto updatedUser = userService.assignCompanyToEmployer(userId, companyId);
         return ResponseEntity.ok(updatedUser);
     }

     @PutMapping(path = "/profile/jobseeker", version="1.0",
             consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProfileDto> createOrUpdateProfile(
            @RequestPart(value="profile") String profileJson,
            @RequestPart(value="profilePicture", required = false) MultipartFile profilePicture,
            @RequestPart(value="resume", required = false) MultipartFile resume,
            Authentication authentication) throws JsonProcessingException {

        String userEmail = authentication.getName();

        ProfileDto savedProfile = userService
                 .createOrUpdateProfile(userEmail, profileJson, profilePicture, resume);

        return ResponseEntity.ok(savedProfile);

     }

     @GetMapping(path = "/profile/jobseeker", version = "1.0")
    public ResponseEntity<ProfileDto> getProfile(Authentication authentication) {

         String userEmail = authentication.getName();

         ProfileDto profileDto = userService.getProfile(userEmail);

         return ResponseEntity.ok(profileDto);

     }

     @GetMapping(path = "/profile/picture/jobseeker", version = "1.0")
    public ResponseEntity<byte[]> getProfilePicture(Authentication authentication) {

        String userEmail = authentication.getName();

         ProfileDto profileDto = userService.getProfilePicture(userEmail);

         byte[] picture = profileDto.profilePicture();

         if (picture == null || picture.length == 0) {
             return ResponseEntity.notFound().build();
         }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(profileDto.profilePictureType()));
        headers.setContentLength(picture.length);

        return new ResponseEntity<>(picture, headers, HttpStatus.OK);


     }

    @GetMapping(path = "/profile/resume/jobseeker", version = "1.0")
    public ResponseEntity<byte[]> getResume(Authentication authentication) {

        String userEmail = authentication.getName();

        ProfileDto profileDto = userService.getResume(userEmail);

        byte[] resume = profileDto.resume();

        if (resume == null || resume.length == 0) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(profileDto.resumeType()));
        headers.setContentLength(resume.length);
        headers.setContentDispositionFormData("attachment", profileDto.resumeName());

        return new ResponseEntity<>(resume, headers, HttpStatus.OK);


    }

    @PostMapping(path ="/saved-jobs/{jobId}/jobseeker", version="1.0")
    public ResponseEntity<JobDto> saveJob(@PathVariable Long jobId,
                                          Authentication authentication) {

        String userEmail = authentication.getName();

        JobDto savedJob = userService.saveJob(userEmail, jobId);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedJob);

    }

    @DeleteMapping(path ="/saved-jobs/{jobId}/jobseeker", version="1.0")
    public ResponseEntity<String> unSaveJob(@PathVariable Long jobId,
                                          Authentication authentication) {

        String userEmail = authentication.getName();

        userService.unsaveJob(userEmail, jobId);

        return ResponseEntity.ok().body("Job unsaved successfully");

    }

    @GetMapping(path ="/saved-jobs/jobseeker", version = "1.0")
    public ResponseEntity<List<JobDto>> getSavedJob(Authentication authentication) {

        String userEmail = authentication.getName();

        List<JobDto> savedJobDtos =  userService.getSavedJobs(userEmail);

        return ResponseEntity.ok(savedJobDtos);

    }

    @PostMapping(path ="/job-applications/jobseeker", version = "1.0")
    public ResponseEntity<JobApplicationDto> applyForJob(
            @RequestBody @Valid ApplyJobRequestDto applyJobRequestDto,
            Authentication authentication
            ) {

        String userEmail = authentication.getName();

        JobApplicationDto application = userService.applyForJob(userEmail, applyJobRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(application);


    }


    @DeleteMapping(path = "/job-applications/{jobId}/jobseeker", version = "1.0")
    public ResponseEntity<String> withdrawApplication(
            @PathVariable Long jobId,
            Authentication authentication
    ) {

        String userEmail = authentication.getName();

        userService.withdrawApplication(userEmail, jobId);

        return ResponseEntity.status(HttpStatus.OK).body("Application withdrawn successfully");

    }

    @GetMapping(path = "/job-applications/jobseeker")
    public ResponseEntity<List<JobApplicationDto>> getJobSeekerApplications(Authentication authentication) {

        String userEmail = authentication.getName();

        List<JobApplicationDto> applications = userService.getJobSeekerApplications(userEmail);

        return ResponseEntity.ok(applications);

    }




}
