package com.sns.waloauthenticationservice.services.impl;

import com.sns.waloauthenticationservice.model.AdminDetails;
import com.sns.waloauthenticationservice.repository.AdminDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

import static com.sns.waloauthenticationservice.Utils.Constants.*;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    AdminDetailsRepository adminDetailsRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        AdminDetails adminDetails = new AdminDetails();
        Optional<AdminDetails> adminCred = adminDetailsRepository.findByEmail(username);
        if (adminCred.isPresent()) {
            if (adminCred.get().getEmail().equals(username)) {
                return new User(username, adminCred.get().getPassword(), new ArrayList<>());
            }
        }
        else if (DEFAULT_USER.equals(username)) {
            adminDetails.setEmail(DEFAULT_USER);
            adminDetails.setPassword(bCryptPasswordEncoder.encode(DEFAULT_PASSWORD));
            adminDetails.setAuthority(true);
            Optional<AdminDetails> optionalAdminDetails = adminDetailsRepository.findByEmail(DEFAULT_USER);
            if(!optionalAdminDetails.isPresent())
            {
                adminDetailsRepository.save(adminDetails);
            }
            return new User(username, bCryptPasswordEncoder.encode(DEFAULT_PASSWORD),new ArrayList<>());
        }
        throw new UsernameNotFoundException(USER_NOT_FOUND + username);
    }
}
