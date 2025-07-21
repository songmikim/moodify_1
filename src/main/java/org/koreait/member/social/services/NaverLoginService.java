package org.koreait.member.social.services;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.koreait.global.exceptions.script.AlertRedirectException;
import org.koreait.global.libs.Utils;
import org.koreait.member.entities.Member;
import org.koreait.member.repositories.MemberRepository;
import org.koreait.member.services.MemberInfoService;
import org.koreait.member.social.constants.SocialType;
import org.koreait.member.social.entities.AuthToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

@Lazy
@Service
@RequiredArgsConstructor
public class NaverLoginService implements SocialLoginService {
    private final Utils utils;
    private final RestTemplate restTemplate;
    private final MemberRepository memberRepository;
    private final MemberInfoService infoService;
    private final HttpSession session;

    @Value("${social.naver.apikey}")
    private String apiKey;

    @Value("${social.naver.secret}")
    private String secretKey;

    @Override
    public String getToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", apiKey);
        body.add("client_secret", secretKey);
        body.add("redirect_uri", utils.getUrl("/member/social/callback/naver"));
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        String requestUrl = "https://nid.naver.com/oauth2.0/token";

        ResponseEntity<AuthToken> response = restTemplate.exchange(URI.create(requestUrl), HttpMethod.POST, request, AuthToken.class);

        checkSuccess(response.getStatusCode());

        // access Token으로 회원정보 조회
        AuthToken authToken = response.getBody();
        requestUrl = "https://openapi.naver.com/v1/nid/me";
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(authToken.getAccessToken());

        request = new HttpEntity<>(headers);
        ResponseEntity<Map> res = restTemplate.exchange(URI.create(requestUrl), HttpMethod.POST, request, Map.class);

        checkSuccess(res.getStatusCode());

        Map resBody = res.getBody();
        Map<String, Object> responseMap = (Map<String, Object>) resBody.get("response");

        if (responseMap == null || responseMap.get("id") == null) {
            throw new IllegalStateException("네이버 사용자 정보에서 id를 가져올 수 없습니다.");
        }

        String id = (String) responseMap.get("id");
        return id;
    }

    @Override
    public boolean login(String token) {
        Member member = memberRepository.findBySocialTypeAndSocialToken(SocialType.NAVER, token);
        if (member == null) {
            return false;
        }

        UserDetails userDetails = infoService.loadUserByUsername(member.getEmail());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication); // 로그인 처리

        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        return true;
    }

    @Override
    public boolean exists(String token) {

        return memberRepository.existsBySocialTypeAndSocialToken(SocialType.NAVER, token);
    }

    @Override
    public String getLoginUrl(String redirectUrl) {

        String redirectUri = utils.getUrl("/member/social/callback/naver");

        return String.format("https://nid.naver.com/oauth2.0/authorize?client_id=%s&response_type=code&redirect_uri=%s&state=%s",
                apiKey, redirectUri, Objects.requireNonNullElse(redirectUrl, ""));
    }

    private void checkSuccess(HttpStatusCode status) {
        if (!status.is2xxSuccessful()) {
            throw new AlertRedirectException(utils.getMessage("UnAuthorized.social"), "/member/login", HttpStatus.UNAUTHORIZED);
        }
    }
}
