package com.sns.waloauthenticationservice.Utils;

import com.sns.waloauthenticationservice.services.GenerateResetPassCode;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sns.waloauthenticationservice.Utils.Constants.EMAIL_VALIDATION;
import static com.sns.waloauthenticationservice.Utils.Constants.PASSWORD_VALIDATION;

public class Utility implements GenerateResetPassCode {

    public static boolean validatePassword(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_VALIDATION);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
    public static boolean validateEmailId(String mailId){
    Pattern pattern=Pattern.compile(EMAIL_VALIDATION);
    Matcher matcher=pattern.matcher(mailId);
    return  matcher.matches() && mailId.contains("@sece.ac.in");
}
    @Override
    public String generateCode() {
        Random random = new Random();
        int max = 999999;
        int min = 111111;
        return String.valueOf(random.nextInt(max - min) + min);
    }
}
