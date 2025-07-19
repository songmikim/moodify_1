package org.koreait.member.controllers;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.koreait.member.social.constants.SocialType;
import org.springframework.util.StringUtils;

@Data
public class RequestJoin {
    @Email
    @NotBlank
    private String email;

    @Size(min=8)
    private String password;

    private String confirmPassword;

    @NotBlank
    private String name;

    @NotBlank
    private String mobile;

    @AssertTrue
    private boolean termsAgree;

    private SocialType socialType;
    private String socialToken;

    public boolean isSocial() {
        return socialType != null && socialType != SocialType.NONE && StringUtils.hasText(socialToken);
    }
}
