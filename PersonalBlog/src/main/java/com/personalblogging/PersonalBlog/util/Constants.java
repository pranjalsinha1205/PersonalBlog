package com.personalblogging.PersonalBlog.util;

import org.springframework.context.annotation.Configuration;

@Configuration
public class Constants {
    public static final String SUCCESS_CODE = "200";
    public static final String FAILURE_CODE = "500";

    public static final String usernameRegex = "^(?=.*[a-z])[a-zA-Z0-9_+&*-]{3,}$";

    public static final String passwordRegex = "^.{8,}$";
}
