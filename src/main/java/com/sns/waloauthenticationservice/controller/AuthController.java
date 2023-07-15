package com.sns.waloauthenticationservice.controller;


import com.sns.waloauthenticationservice.config.JwtTokenUtil;
import com.sns.waloauthenticationservice.dtos.JwtRequest;
import com.sns.waloauthenticationservice.dtos.JwtResponse;
import com.sns.waloauthenticationservice.services.impl.JwtUserDetailsService;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


import static com.sns.waloauthenticationservice.Utils.Constants.CLAIMS_ATTR;
import static com.sns.waloauthenticationservice.Utils.Constants.SUB;
import static com.sns.waloauthenticationservice.Utils.Urls.LOGIN;
import static com.sns.waloauthenticationservice.Utils.Urls.REFRESH_TOKEN;

@RestController
@CrossOrigin
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtUserDetailsService userDetailsService;

    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String loginUser()
    {
        return "Came inside Login";
    }
    @RequestMapping(value = LOGIN, method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }
    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
    @RequestMapping(value = REFRESH_TOKEN, method = RequestMethod.GET)
    public ResponseEntity<?> refreshToken(HttpServletRequest request) throws Exception {
        // From the HttpRequest get the claims
        DefaultClaims claims = (io.jsonwebtoken.impl.DefaultClaims) request.getAttribute(CLAIMS_ATTR);

        Map<String, Object> expectedMap = getMapFromIoJsonwebtokenClaims(claims);
        String token = jwtTokenUtil.doGenerateRefreshToken(expectedMap, expectedMap.get(SUB).toString());
        return ResponseEntity.ok(new JwtResponse(token));
    }

    //todo: Fetch Requested Password.
//    @GetMapping(value = "/requested-password")
//    public BaseResponse<List<?>> getRequestedPassword(@RequestHeader(AUTHORIZATION) String token){
//
//    }

    public Map<String, Object> getMapFromIoJsonwebtokenClaims(DefaultClaims claims) {
        Map<String, Object> expectedMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            expectedMap.put(entry.getKey(), entry.getValue());
        }
        return expectedMap;
    }
}
