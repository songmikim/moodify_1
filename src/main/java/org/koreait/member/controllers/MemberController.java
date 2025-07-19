package org.koreait.member.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.global.annotations.ApplyCommonController;
import org.koreait.global.libs.Utils;
import org.koreait.member.services.JoinService;
import org.koreait.member.social.constants.SocialType;
import org.koreait.member.social.services.KakaoLoginService;
import org.koreait.member.validators.JoinValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@ApplyCommonController
@RequestMapping("/member")
@SessionAttributes("requestLogin")
public class MemberController {

    private final Utils utils;
    private final JoinValidator joinValidator;
    private final JoinService joinService;
    private final KakaoLoginService kakaoLoginService;

    @ModelAttribute("addCss")
    public List<String> addCss() {
        return List.of("member/style");
    }

    @ModelAttribute("requestLogin")
    public RequestLogin requestLogin() {
        return new RequestLogin();
    }

    // 회원가입 양식
    @GetMapping("/join")
    public String join(@ModelAttribute RequestJoin form, Model model,
                       @SessionAttribute(name="socialType", required = false) SocialType type,
                       @SessionAttribute(name="socialToken", required = false) String socialToken) {

        commonProcess("join", model);

        form.setSocialType(type);
        form.setSocialToken(socialToken);

        return utils.tpl("member/join");
    }

    // 회원가입 처리
    @PostMapping("/join")
    public String joinPs(@Valid RequestJoin form, Errors errors, Model model) {
        commonProcess("join", model);

        joinValidator.validate(form, errors);

        if (errors.hasErrors()) {
            return utils.tpl("member/join");
        }

        joinService.process(form);

        // 회원가입 성공시
        return "redirect:/member/login";
    }

    @GetMapping("/login")
    public String login(@ModelAttribute RequestLogin form, Errors errors, Model model) {
        commonProcess("login", model);

        /* 검증 실패 처리 S */
        List<String> fieldErrors = form.getFieldErrors();
        if (fieldErrors != null) {
            fieldErrors.forEach(s -> {
                // 0 - 필드, 1 - 에러코드
                String[] value = s.split("_");
                errors.rejectValue(value[0], value[1]);
            });

        }
        List<String> globalErrors = form.getGlobalErrors();
        if (globalErrors != null) {
            globalErrors.forEach(errors::reject);
        }
        /* 검증 실패 처리 E */

        /* 소셜 로그인 URL */
        model.addAttribute("kakaoLoginUrl", kakaoLoginService.getLoginUrl(form.getRedirectUrl()));

        return utils.tpl("member/login");
    }

    /**
     * 비밀번호 만료시 변경 페이지
     *
     * @param model
     * @return
     */
    @GetMapping("/password")
    public String password(Model model) {
        commonProcess("password", model);

        return utils.tpl("member/password");
    }

    /**
     * 현재 컨트롤러의 공통 처리 부분
     *
     * @param mode
     * @param model
     */
    private void commonProcess(String mode, Model model) {
        mode = StringUtils.hasText(mode) ? mode : "join";
        String pageTitle = "";
        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();

        if (mode.equals("join")) { // 회원 가입 공통 처리
            addCommonScript.add("fileManager");
            addScript.add("member/join");
            pageTitle = utils.getMessage("회원가입");

        } else if (mode.equals("login")) { // 로그인 공통 처리
            pageTitle = utils.getMessage("로그인");
        }

        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        model.addAttribute("pageTitle", pageTitle);
    }

//    @ResponseBody
//    @GetMapping("/test")
//    public void test(Principal principal) {
//        String email = principal.getName();
//        System.out.println("email:" + email);
//    }

//    @ResponseBody
//    @GetMapping("/test")
//    public void test(@AuthenticationPrincipal MemberInfo memberInfo) {
//        System.out.println("memberInfo:" + memberInfo);
//    }

//    @ResponseBody
//    @GetMapping("/test")
//    public void test() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        System.out.println("인증상태:" + auth.isAuthenticated());
//        System.out.println("Principle:" + auth.getPrincipal());
//    }

//    @ResponseBody
//    @GetMapping("/test")
//    public void test() {
//        System.out.printf("로그인:%s, 관리자여부:%s, 회원정보:%s%n", memberUtil.isLogin(), memberUtil.isAdmin(), memberUtil.getMember());
//    }
}
