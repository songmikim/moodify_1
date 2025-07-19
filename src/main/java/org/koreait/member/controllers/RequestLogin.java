package org.koreait.member.controllers;

import lombok.Data;

import java.util.List;

@Data
public class RequestLogin {
    private String email;
    private String password;
    private boolean autoLogin;
    private String redirectUrl;
    private List<String> fieldErrors; // 필드명_에러코드
    private List<String> globalErrors;
}
