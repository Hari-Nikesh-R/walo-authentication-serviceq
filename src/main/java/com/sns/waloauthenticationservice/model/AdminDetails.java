package com.sns.waloauthenticationservice.model;

import com.sns.waloauthenticationservice.dtos.Gender;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "adminDetails")
public class AdminDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    private String email;
    private String firstName;
    private String lastName;
    private Integer dob;
    private Gender gender;
    private Integer academicYear;
    private String phoneNumber;
    private String password;
    private String bloodGroup;
    private String organizationalAddress;
    private boolean authority;
    private Date createdAt;
}
