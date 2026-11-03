package com.eazybytes.jobportal.job.service.impl;

import com.eazybytes.jobportal.JobApplicationDto;
import com.eazybytes.jobportal.dto.JobDto;
import com.eazybytes.jobportal.dto.UpdateJobApplicationDto;
import com.eazybytes.jobportal.entity.Job;
import com.eazybytes.jobportal.entity.JobApplication;
import com.eazybytes.jobportal.entity.JobPortalUser;
import com.eazybytes.jobportal.job.service.IJobService;
import com.eazybytes.jobportal.repository.JobApplicationRepository;
import com.eazybytes.jobportal.repository.JobPortalUserRepository;
import com.eazybytes.jobportal.repository.JobRepository;
import com.eazybytes.jobportal.util.ApplicationUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobServiceImpl implements IJobService {


    private final JobRepository jobRepository;
    private final JobPortalUserRepository  userRepository;
    private final JobApplicationRepository jobApplicationRepository;


    @Override
    public List<JobDto> getEmployerJobs(String employerEmail) {

        JobPortalUser employer = userRepository.findJobPortalUserByEmail(employerEmail)
                .orElseThrow(() -> new RuntimeException("Employer not found"));

        if (employer.getCompany() == null) {
            throw new RuntimeException("Employer does not have a company assigned");
        }

        List<Job> jobs = employer.getCompany().getJobs();

        return jobs.stream()
                .map(job -> ApplicationUtility.transformJobToDto(job))
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public JobDto updateJobStatus(Long jobId, String status, String employerEmail) {

        if (!status.equals("ACTIVE") && !status.equals("CLOSED") && !status.equals("DRAFT")) {
            throw new RuntimeException("Invalid status value. Must be ACTIVE, CLOSED, or DRAFT");
        }

        JobPortalUser employer = userRepository.findJobPortalUserByEmail(employerEmail)
                .orElseThrow(() -> new RuntimeException("Employer not found"));

        if (employer.getCompany() == null) {
            throw new RuntimeException("Employer does not have a company assigned");
        }

        Job job = employer.getCompany().getJobs().stream().filter(j -> j.getId().equals(jobId)).findFirst()
                .orElseThrow(() -> new RuntimeException("Job not found"));

        job.setStatus(status);
        /*
        And here there is no need of invoking the save method and
        passing these updated job entity object.
         Because as we discussed previously, s
         ince this is a entity object that is being tracked by Hibernate session,
         when we're done executing this method, the Hibernate, it is automatically going to commit the  transaction to the database.
         */


        return ApplicationUtility.transformJobToDto(job);
    }

    @Override
    @Transactional
    public JobDto createJob(JobDto jobDto, String employerEmail) {

        JobPortalUser employer = userRepository.findJobPortalUserByEmail(employerEmail)
                .orElseThrow(() -> new RuntimeException("Employer not found"));

        if (employer.getCompany() == null) {
            throw new RuntimeException("Employer does not have a company assigned");
        }

        Job job = transformDtoToJobEntity(jobDto);

        job.setPostedDate(Instant.now());
        job.setApplicationsCount(0);
        job.setStatus("DRAFT");
        job.setCompany(employer.getCompany());


        /*
        Once all the required details are populated inside the job entity object,
         we need to invoke the save method. So here we need to make sure to invoke the save method
         because the job entity record is a brand new record object that we have created.
         This is not the one that we loaded from the database.
         That's why the job entity object is not going to be tracked by the Hibernate session.
         Since Hibernate is not tracking that object, we need to make sure to invoke the save method manually
         */
        Job savedJob = jobRepository.save(job);


        return ApplicationUtility.transformJobToDto(savedJob);

    }

    @Override
    public List<JobApplicationDto> getApplicationsByJobForEmployer(Long jobId) {

        List<JobApplication>  applications = jobApplicationRepository.findByJobIdOrderByAppliedAtAsc(jobId);

        return applications
                .stream()
                .map(jobApplication -> ApplicationUtility.mapToJobApplicationDto(jobApplication))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean updateJobApplication(UpdateJobApplicationDto updateJobApplicationDto) {

        int updatedRows = jobApplicationRepository.updateStatusAndNotesById(
                updateJobApplicationDto.status().name(),
                updateJobApplicationDto.notes(),
                updateJobApplicationDto.applicationId(),
                ApplicationUtility.getLoggedInUser());

        return  updatedRows > 0;

    }

    private Job transformDtoToJobEntity(JobDto jobDto) {

        Job job = new Job();
        BeanUtils.copyProperties(jobDto, job);
        return job;
    }


}
