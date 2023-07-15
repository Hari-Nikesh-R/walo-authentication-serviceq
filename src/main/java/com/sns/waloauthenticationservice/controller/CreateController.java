package com.sns.waloauthenticationservice.controller;

import com.sns.waloauthenticationservice.Utils.Utility;
import com.sns.waloauthenticationservice.config.JwtTokenUtil;
import com.sns.waloauthenticationservice.dtos.BaseResponse;
import com.sns.waloauthenticationservice.model.AdminDetails;
import com.sns.waloauthenticationservice.services.AdminService;
import com.sns.waloauthenticationservice.services.FetchInfoService;
import com.sns.waloauthenticationservice.services.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static com.sns.waloauthenticationservice.Utils.Constants.AUTHORIZATION;
import static com.sns.waloauthenticationservice.Utils.Urls.USER_URL;

@RestController
@CrossOrigin("*")
@RequestMapping(value = "/register")
public class CreateController {

    @Autowired
    RegisterService<AdminDetails> createAdminService;

    @Autowired
    FetchInfoService<AdminDetails,Integer> adminDetailsIntegerFetchInfoService;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    AdminService adminService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @PostMapping(value = USER_URL)
    public BaseResponse<AdminDetails> registerAdmin(@RequestBody AdminDetails adminDetails, @RequestHeader(AUTHORIZATION) String token)
    {
        AdminDetails details=null;
        if(Utility.validatePassword(adminDetails.getPassword()) && Utility.validateEmailId(adminDetails.getEmail())) {
            details = createAdminService.save(adminDetails);
        }
        else{
            return new BaseResponse<>(HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),HttpStatus.NOT_ACCEPTABLE.value(), false,"Invalid Format",null);
        }
        if(Objects.nonNull(details)){
            return new BaseResponse<>(HttpStatus.CREATED.getReasonPhrase(), HttpStatus.OK.value(),true,"",details);
        }
        else{
            return new BaseResponse<>(HttpStatus.ALREADY_REPORTED.getReasonPhrase(), HttpStatus.ALREADY_REPORTED.value(), false, "User Already Exist", null);
        }
    }
    @PostMapping(value = "/verify")
    public BaseResponse<String> verifyRegistration(@RequestParam("code") String code, @RequestBody AdminDetails adminDetails)
    {
        return adminService.verifyCode(code,adminDetails);
    }
    @PostMapping
    public BaseResponse<String> register(@RequestBody AdminDetails adminDetails)
    {
        try {
            if(Utility.validatePassword(adminDetails.getPassword()) && Utility.validateEmailId(adminDetails.getEmail())) {
                return adminService.sendCodeToMail(adminDetails.getEmail());
            }
            else{
                return new BaseResponse<>("Only SECE Organization member is allowed", HttpStatus.NO_CONTENT.value(), false,"Cannot create",null);
            }
        }
        catch (Exception exception)
        {
            BaseResponse<String> baseResponse = new BaseResponse<>(exception.toString(), HttpStatus.INTERNAL_SERVER_ERROR.value(), false, exception.getMessage(), null);
            if (baseResponse.getError().contains("401")) {
                baseResponse.setCode(401);
            }
            return baseResponse;
        }
    }
    private HttpEntity<String> setTokenInHeaders(String token){
        HttpHeaders httpHeaders = getHeaders();
        httpHeaders.set(AUTHORIZATION, token);
        return new HttpEntity<>(httpHeaders);
    }
    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }
}
