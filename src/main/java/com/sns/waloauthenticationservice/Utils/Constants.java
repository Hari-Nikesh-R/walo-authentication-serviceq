package com.sns.waloauthenticationservice.Utils;


public interface Constants {
    String USER_NOT_FOUND = "User not found with username: ";
    String ADMIN = "ADMIN";
    String DEFAULT_USER = "hari.nikesh.r.cce@sece.ac.in";
    String DEFAULT_PASSWORD = "Admin@123";

    String ADMIN_ACCESS = "hasAuthority('ADMIN')";
    String AUTHORIZATION = "Authorization";
    String CLAIMS_ATTR = "claims";
    String PASSWORD_VALIDATION = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
    String SUB = "sub";
    String EMAIL_VALIDATION="^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    String USERNAME_SUFFICE = "@sece";
    String UPDATE_PASSWORD = "Password Updated Successfully";
    String UPDATE_PASSWORD_FAILED = "Failed to Update password";
    String RESET_SUCCESSFUL = "Password Reset Successful";

}
