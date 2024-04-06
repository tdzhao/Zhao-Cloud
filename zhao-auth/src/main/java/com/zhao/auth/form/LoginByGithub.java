package com.zhao.auth.form;

import lombok.Data;

@Data
public class LoginByGithub extends LoginBody{
    private String code;
    private String uuid;
    private String source;

}
