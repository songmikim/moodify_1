package org.koreait.global.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.koreait.member.libs.MemberUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@RequiredArgsConstructor
public class CommonInterceptor implements HandlerInterceptor {
    private final MemberUtil memberUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        clearSocialToken(request); // 소셜 세션 값 제거

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            /* 로그인 회원 정보 유지 S */
            modelAndView.addObject("isLogin", memberUtil.isLogin());
            modelAndView.addObject("isAdmin", memberUtil.isAdmin());
            modelAndView.addObject("loggedMember", memberUtil.getMember());
            /* 로그인 회원 정보 유지 E */
        }
    }

    /**
     * socialType, socialToken은 소셜 로그인 시도시 /member/join 쪽에만 유지하면 되므로
     * 나머지 주소에서는 해당 세션 값을 제거
     *
     * @param request
     */
    private void clearSocialToken(HttpServletRequest request) {
        String url = request.getRequestURI();
        if (!url.contains("/member/join")) {
            HttpSession session = request.getSession();
            session.removeAttribute("socialType");
            session.removeAttribute("socialToken");
        }
    }

}

