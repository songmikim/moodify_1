package org.koreait.member.social.services;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@RequiredArgsConstructor
public class NaverLoginService implements SocialLoginService {
    @Override
    public String getToken(String code) {
        return "";
    }

    @Override
    public boolean login(String token) {
        return false;
    }

    @Override
    public boolean exists(String token) {
        return false;
    }

    @Override
    public String getLoginUrl(String redirectUrl) {
        return "";
    }
}
