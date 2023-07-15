package com.sns.waloauthenticationservice.services;

import com.sns.waloauthenticationservice.dtos.Authority;
import com.sns.waloauthenticationservice.dtos.BaseResponse;
import com.sns.waloauthenticationservice.model.AdminDetails;

import java.util.List;

public interface AdminService {
    BaseResponse<String> sendCodeToMail(String email);
    BaseResponse<String> verifyCode(String code, AdminDetails adminDetails);
    String updateAuthority(Authority authority);
    Boolean isAuthorizedUser(String email);
    List<Authority> getAuthority();


}
