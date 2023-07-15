package com.sns.waloauthenticationservice.controller;

import com.sns.waloauthenticationservice.Utils.Urls;
import com.sns.waloauthenticationservice.config.JwtTokenUtil;
import com.sns.waloauthenticationservice.dtos.Authority;
import com.sns.waloauthenticationservice.dtos.BaseResponse;
import com.sns.waloauthenticationservice.dtos.UpdatePassword;
import com.sns.waloauthenticationservice.model.AdminDetails;
import com.sns.waloauthenticationservice.services.AdminService;
import com.sns.waloauthenticationservice.services.FetchInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

import static com.sns.waloauthenticationservice.Utils.Constants.AUTHORIZATION;
import static com.sns.waloauthenticationservice.Utils.Constants.DEFAULT_USER;
import static com.sns.waloauthenticationservice.Utils.Urls.*;


@RestController
@RequestMapping(USER_URL)
public class AdminController {
    @Autowired
    FetchInfoService<AdminDetails,Integer> adminDetailsIntegerFetchInfoService;
    @Autowired
    AdminService adminService;
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @GetMapping(value = INFO)
    public BaseResponse<AdminDetails> getAdminDetail(@RequestHeader(AUTHORIZATION) String token){
        token = token.replace("Bearer ","");
       String email = jwtTokenUtil.getUsernameFromToken(token);
       AdminDetails adminDetails = adminDetailsIntegerFetchInfoService.getInfoByEmail(email);
        if(Objects.nonNull(adminDetails))
        {
            return new BaseResponse<>(HttpStatus.ACCEPTED.toString(), HttpStatus.OK.value(), true,"",adminDetails);
        }
        return new BaseResponse<>(HttpStatus.NO_CONTENT.toString(), HttpStatus.NO_CONTENT.value(), false,"No Admin User Found",null);
    }
    @PostMapping(value = VALIDATE_EMAIL)
    public Boolean isAdminAvailable(@RequestBody AdminDetails adminDetails)
    {
        return adminDetailsIntegerFetchInfoService.validateByEmail(adminDetails.getEmail());
    }
    @GetMapping(value = IS_ADMIN)
    public Boolean isAdminUser(@RequestHeader(AUTHORIZATION) String token){
        token = token.replace("Bearer ","");
        String email = jwtTokenUtil.getUsernameFromToken(token);
        return adminDetailsIntegerFetchInfoService.isAuthorized(email);
    }

    @GetMapping(value = FETCH_ID)
    public synchronized Integer getAdminId(@RequestHeader(AUTHORIZATION) String token){
        token = token.replace("Bearer ","");
        String email =jwtTokenUtil.getUsernameFromToken(token);
        Integer adminInfoId = adminDetailsIntegerFetchInfoService.getId(email);
        if(Objects.nonNull(adminInfoId)) {
           return adminInfoId;
        }
        return -1;
    }
    @PutMapping(value = Urls.UPDATE_PASSWORD)
        public BaseResponse<String> updatePassword(@RequestBody UpdatePassword updatePassword, @RequestHeader(AUTHORIZATION) String token){
        HttpEntity<String> entity = setTokenInHeaders(token);
        Integer id = restTemplate.exchange(AUTHENTICATION_URL + "/user/fetch-id", HttpMethod.GET,entity,Integer.class).getBody();
        updatePassword.setId(id);
        String isUpdated = adminDetailsIntegerFetchInfoService.changePassword(updatePassword);
        if(Objects.nonNull(isUpdated))
        {
            return new BaseResponse<>("Updated", HttpStatus.OK.value(), true,"",isUpdated);
        }
        return new BaseResponse<>(HttpStatus.UPGRADE_REQUIRED.toString(), HttpStatus.UPGRADE_REQUIRED.value(), false,"Failed to update password",null);
    }

    @PutMapping(value = CHANGE_PASSWORD)
    public BaseResponse<String> updatePassword(@RequestBody UpdatePassword updatePassword){

        String isUpdated = adminDetailsIntegerFetchInfoService.forgotPasswordReset(updatePassword);
        if(Objects.nonNull(isUpdated))
        {
            return new BaseResponse<>("Updated", HttpStatus.OK.value(), true,"",isUpdated);
        }
        return new BaseResponse<>(HttpStatus.UPGRADE_REQUIRED.toString(), HttpStatus.UPGRADE_REQUIRED.value(), false,"Failed to update password",null);
    }

    @PutMapping(value = UPDATE_PROFILE)
    public BaseResponse<AdminDetails> updateAdminDetails(@RequestBody AdminDetails adminDetails, @RequestHeader(AUTHORIZATION) String token)
    {
        token = token.replace("Bearer ","");
        String email = jwtTokenUtil.getUsernameFromToken(token);
        AdminDetails updatedDetail = adminDetailsIntegerFetchInfoService.updateProfile(adminDetails,email);
        if(Objects.nonNull(updatedDetail))
        {
            return new BaseResponse<>("Update Successful",HttpStatus.OK.value(),true,"",updatedDetail);
        }
        return new BaseResponse<>("Not Updated",HttpStatus.NON_AUTHORITATIVE_INFORMATION.value(), false,"updateDetails is null",null);
    }

    @PutMapping(value = "/authority")
    public BaseResponse<String> grantAuthority(@RequestHeader(AUTHORIZATION) String token, @RequestBody Authority authority){
        try {
            token = token.replace("Bearer ", "");
            String email = jwtTokenUtil.getUsernameFromToken(token);
            if (email.equals(DEFAULT_USER)) {
                return new BaseResponse<>("Updated Successfully", HttpStatus.OK.value(), true, "", adminService.updateAuthority(authority));
            } else {
                return new BaseResponse<>("Not Authorized User", HttpStatus.NON_AUTHORITATIVE_INFORMATION.value(), false, "Update Unsuccessful", null);
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

    //todo: Need to refurnish
    @GetMapping(value = IS_AUTHORIZED)
    public BaseResponse<Boolean> isAuthorizeUser(@RequestHeader(AUTHORIZATION) String token){
        try {
            token = token.replace("Bearer ", "");
            String email = jwtTokenUtil.getUsernameFromToken(token);
            Boolean isAuthorized = adminService.isAuthorizedUser(email);
            if(isAuthorized){
                return new BaseResponse<>("Authorized User",HttpStatus.OK.value(),true, "", true);
            }
            else{
                return new BaseResponse<>("Not Authorized User", HttpStatus.NO_CONTENT.value(), false, "Not authorized to create", false);
            }
        }
        catch (Exception exception)
        {
            BaseResponse<Boolean> baseResponse = new BaseResponse<>(exception.toString(), HttpStatus.INTERNAL_SERVER_ERROR.value(), false, exception.getMessage(), null);
            if (baseResponse.getError().contains("401")) {
                baseResponse.setCode(401);
            }
            return baseResponse;
        }
    }

    @GetMapping(value = "/get-email")
    public String getEmailByToken(@RequestHeader(AUTHORIZATION) String token){
        token = token.replace("Bearer ", "");
        return jwtTokenUtil.getUsernameFromToken(token);
    }

    @GetMapping(value = GET_ALL_USER)
    public BaseResponse<List<Authority>> getAuthorizedUser(){
        try{
            List<Authority> authorities = adminService.getAuthority();
            if(Objects.nonNull(authorities)) {
                return new BaseResponse<>("All Users", HttpStatus.OK.value(), true, "", authorities);
            }
            else{
                return new BaseResponse<>("No Authorized Users", HttpStatus.NO_CONTENT.value(), false, "No Users found",null);
            }
        }
        catch (Exception exception)
        {
            BaseResponse<List<Authority>> baseResponse = new BaseResponse<>(exception.toString(), HttpStatus.INTERNAL_SERVER_ERROR.value(), false, exception.getMessage(), null);
            if (baseResponse.getError().contains("401")) {
                baseResponse.setCode(401);
            }
            return baseResponse;
        }
    }

    private HttpEntity<String> setTokenInHeaders(String token){
        HttpHeaders httpHeaders = getHeaders();
        httpHeaders.set(AUTHORIZATION,token);
        return new HttpEntity<>(httpHeaders);
    }
    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

}
