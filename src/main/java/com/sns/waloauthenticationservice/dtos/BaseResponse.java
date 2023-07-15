package com.sns.waloauthenticationservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {
    private String result;
    private int code;
    private Boolean success;
    private String error;
    private T value;
}
