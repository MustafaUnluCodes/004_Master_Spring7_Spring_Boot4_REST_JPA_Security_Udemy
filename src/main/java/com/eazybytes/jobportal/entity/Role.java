package com.eazybytes.jobportal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roles")
@Getter @Setter
public class Role extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 50)
    @NotNull
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    /* Lesson 132:
    The OneToMany on the Role side is optional.
     If you don’t need to fetch all users by role, don’t include it to avoid heavy bi-directional mapping.
     */
//    @OneToMany(mappedBy = "role")
//    private List<JobPortalUser> jobPortalUsers = new ArrayList<>();

}