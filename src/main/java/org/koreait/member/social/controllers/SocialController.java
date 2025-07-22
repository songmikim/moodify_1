package org.koreait.member.social.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.koreait.global.annotations.ApplyCommonController;
import org.koreait.member.social.constants.SocialType;
import org.koreait.member.social.services.KakaoLoginService;
import org.koreait.member.social.services.NaverLoginService;
import org.koreait.member.social.services.SocialLoginService;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@ApplyCommonController
@RequiredArgsConstructor
@RequestMapping("/member/social")
public class SocialController {
    private final HttpSession session;
    private final KakaoLoginService kakaoLoginService;
    private final NaverLoginService naverLoginService;

    @GetMapping("/callback/{channel}")
    public String callback(@PathVariable("channel") String type, @RequestParam(value = "code", required = false) String code, @RequestParam(name="state", required = false) String redirectUrl) {

        // channel이나 code가 null일 때 메인으로 리턴
        if (!StringUtils.hasText(type) || !StringUtils.hasText(code)){
            return "redirect:/";
        }

        SocialType socialType = SocialType.valueOf(type.toUpperCase());

        if (socialType == SocialType.NONE)return "redirect:/";
        //enum 예외 처리
        //try {
        //    socialType = SocialType.valueOf(type.toUpperCase());
        //} catch (IllegalArgumentException e) {
        //    // enum에 정의되지 않은 값이면 로그인 페이지로 리디렉트
        //    return "redirect:/";
        //}

        SocialLoginService service = socialType == SocialType.NAVER ? naverLoginService :
                                     socialType == SocialType.KAKAO ? kakaoLoginService : null;


        // 토큰 발급
        String token = service.getToken(code);

        // 로그인 처리
        if (service.login(token)) {
            // 로그인 성공시에는 redirectUrl 또는 메인페이지로 이동
            return "redirect:" + (StringUtils.hasText(redirectUrl) ? redirectUrl : "/");
        }

        // 로그인 실패시에는 아직 소셜 회원으로 가입된 것이 아니므로 가입 화면 이동

        session.setAttribute("socialType", socialType);
        session.setAttribute("socialToken", token);

        return "redirect:/member/join";
    }
}
