package com.sns.waloauthenticationservice.services.impl;


import com.sns.waloauthenticationservice.Utils.Constants;
import com.sns.waloauthenticationservice.dtos.Authority;
import com.sns.waloauthenticationservice.dtos.BaseResponse;
import com.sns.waloauthenticationservice.dtos.EmailDetails;
import com.sns.waloauthenticationservice.dtos.UpdatePassword;
import com.sns.waloauthenticationservice.model.AdminDetails;
import com.sns.waloauthenticationservice.repository.AdminDetailsRepository;
import com.sns.waloauthenticationservice.services.AdminService;
import com.sns.waloauthenticationservice.services.FetchInfoService;
import com.sns.waloauthenticationservice.services.GenerateResetPassCode;
import com.sns.waloauthenticationservice.services.RegisterService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static com.sns.waloauthenticationservice.Utils.Urls.*;

@Service
public class AdminServiceImpl implements RegisterService<AdminDetails>, FetchInfoService<AdminDetails, Integer>, AdminService {
    @Autowired
    AdminDetailsRepository adminDetailsRepository;
    @Autowired
    GenerateResetPassCode generateResetPassCode;
    @Autowired
    RestTemplate restTemplate;

    private Map<String, String> generatedCode = new HashMap<>();

    @Override
    public AdminDetails save(AdminDetails adminDetails) {
            Optional<AdminDetails> optionalAdminDetails = adminDetailsRepository.findByEmail(adminDetails.getEmail());
            if (optionalAdminDetails.isPresent()) {
                return null;
            }
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String password = bCryptPasswordEncoder.encode(adminDetails.getPassword());
            adminDetails.setPassword(password);
            adminDetails.setCreatedAt(new Date());
            return adminDetailsRepository.save(adminDetails);
    }
    @Override
    public BaseResponse<String> sendCodeToMail(String emailId) {
        generatedCode.put(emailId,generateResetPassCode.generateCode());
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setSubject("Email Confirmation mail");
        emailDetails.setRecipient(emailId);
        emailDetails.setMsgBody("Confirmation code for the creating account is : "+generatedCode.get(emailId));
        String response = restTemplate.postForEntity(MAIL_URL + PASSCODE, emailDetails, String.class).getBody();
        return new BaseResponse<>("", HttpStatus.OK.value(), true, "", response);
    }

    @Override
    public BaseResponse<String> verifyCode(String code, AdminDetails adminDetails) {
            if (code != null) {
                if (code.equals(generatedCode.get(adminDetails.getEmail()))) {
                        save(adminDetails);
                        return new BaseResponse<>("Code verified and Registered successful", HttpStatus.OK.value(), true, "", "Success");
                    } else {
                        return new BaseResponse<>("Wrong Code", HttpStatus.FORBIDDEN.value(), false, "Cannot create account", "Invalid Code");
                    }
                }
        return new BaseResponse<>("Code not verified", HttpStatus.FORBIDDEN.value(), false, "", "Not Verified");
    }
    @Override
    public String updateAuthority(Authority authority) {
        Optional<AdminDetails> optionalAdminDetails =adminDetailsRepository.findByEmail(authority.getEmail());
        if(optionalAdminDetails.isPresent())
        {
            optionalAdminDetails.get().setAuthority(authority.isAuthorized());
            adminDetailsRepository.save(optionalAdminDetails.get());
        }
        return CHANGE_AUTHORITY;
    }

    @Override
    public Boolean isAuthorizedUser(String email) {
        return null;
    }

    @Override
    public List<Authority> getAuthority() {
        List<AdminDetails> adminDetailsList = adminDetailsRepository.findAll();
        List<Authority> authorityList = new ArrayList<>();
        for(AdminDetails adminDetails : adminDetailsList)
        {
            authorityList.add(new Authority(adminDetails.getEmail(), adminDetails.isAuthority()));
        }
        return authorityList;
    }

    @Override
    public List<AdminDetails> getAllInfo() {
        return adminDetailsRepository.findAll();
    }

    @Override
    public Integer getId(String email) {
        Optional<Integer> optionalAdminId = adminDetailsRepository.fetchId(email);
        return optionalAdminId.orElse(null);
    }

    @Override
    public AdminDetails getInfoById(Integer id) {
        Optional<AdminDetails>  optionalAdminDetails = adminDetailsRepository.findById(id);
        if(optionalAdminDetails.isPresent())
        {
            optionalAdminDetails.get().setPassword("");
            return optionalAdminDetails.get();
        }
        return null;
    }

    @Override
    public AdminDetails getInfoByEmail(String email) {
        Optional<AdminDetails> optionalAdminDetails = adminDetailsRepository.findByEmail(email);
        return optionalAdminDetails.orElse(null);
    }

    @Override
    public String changePassword(UpdatePassword updatePassword) {
        generatedCode.remove(updatePassword.getEmail());
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        AdminDetails adminDetails = getInfoById(updatePassword.getId());
        if(Objects.nonNull(adminDetails)) {
            adminDetails.setPassword(bCryptPasswordEncoder.encode(updatePassword.getPassword()));
            adminDetailsRepository.save(adminDetails);
            return Constants.UPDATE_PASSWORD;
        }
        return null;
    }

    @Override
    public String forgotPasswordReset(UpdatePassword updatePassword) {
        Optional<AdminDetails> optionalAdminDetails = adminDetailsRepository.findByEmail(updatePassword.getEmail());
        if(optionalAdminDetails.isPresent())
        {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            optionalAdminDetails.get().setPassword(bCryptPasswordEncoder.encode(updatePassword.getPassword()));
            adminDetailsRepository.save(optionalAdminDetails.get());
            return Constants.UPDATE_PASSWORD;
        }
        return null;
    }

    @Override
    public AdminDetails updateProfile(AdminDetails details, String email) {
        Optional<AdminDetails> optionalAdminDetails = adminDetailsRepository.findByEmail(email);
        if(optionalAdminDetails.isPresent()) {
            details.setId(optionalAdminDetails.get().getId());
            details.setEmail(optionalAdminDetails.get().getEmail());
            details.setPassword(optionalAdminDetails.get().getPassword());
            BeanUtils.copyProperties(details, optionalAdminDetails.get());
            return adminDetailsRepository.save(optionalAdminDetails.get());
        }
        return null;
    }

    @Override
    public Boolean validateByEmail(String email) {
        Optional<AdminDetails> optionalAdminDetails = adminDetailsRepository.findByEmail(email);
        return optionalAdminDetails.isPresent();
    }

    @Override
    public Boolean isAuthorized(String email) {
        Optional<Boolean> optionalAuthorized = adminDetailsRepository.findByAuthority(email);
        return optionalAuthorized.orElse(false);
    }

}
