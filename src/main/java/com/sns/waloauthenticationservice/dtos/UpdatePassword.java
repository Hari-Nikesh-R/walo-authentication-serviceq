package com.sns.waloauthenticationservice.dtos;

import lombok.Data;

@Data
public class UpdatePassword {
    private Integer id;
    private String email;
    private String password;
    private String username;
    private String code;
}
