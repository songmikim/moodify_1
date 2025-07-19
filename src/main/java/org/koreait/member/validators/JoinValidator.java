package org.koreait.member.validators;

import lombok.RequiredArgsConstructor;
import org.koreait.global.validators.MobileValidator;
import org.koreait.global.validators.PasswordValidator;
import org.koreait.member.controllers.RequestJoin;
import org.koreait.member.repositories.MemberRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Lazy
@Component
@RequiredArgsConstructor
public class JoinValidator implements Validator, PasswordValidator, MobileValidator {

    private final MemberRepository repository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(RequestJoin.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        if (errors.hasErrors()) {
            return;
        }
        RequestJoin form = (RequestJoin) target;

        // 소셜 회원가입이 아닌 경우는 비밀번호, 비밀번호 확인이 필수 항목
        if (!form.isSocial()) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotBlank");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "NotBlank");
        }


        /**
         * 1. 이메일 중복 여부
         * 2. 비밀번호 복잡성
         * 3. 비밀번호 확인
         * 4. 휴대폰번호 형식 검증
         */
        if (!form.isSocial()) {
            String password = form.getPassword();
            String confirmPassword = form.getConfirmPassword();


            // 1. 이메일 중복 여부
            if (repository.existsByEmail(form.getEmail())) {
                errors.rejectValue("email", "Duplicated");
            }

            // 2. 비밀번호 복잡성
            if (!checkAlpha(password, false) || !checkNumber(password) || !checkSpecialChars(password)) {
                errors.rejectValue("password", "Complexity");
            }

            // 3. 비밀번호 확인
            if (!password.equals(confirmPassword)) {
                errors.rejectValue("confirmPassword", "Mismatch");
            }
        }

        // 4. 휴대폰번호 형식 검증
        String mobile = form.getMobile();
        if (!checkMobile(mobile)) {
            errors.rejectValue("mobile", "Format");
        }

    }
}
