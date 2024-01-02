package com.example.web.controller;

import com.example.domain.User;
import com.example.service.UserService;
import com.example.web.dto.userDTO.UserRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Security Session 이 따로 존재. 로그인 성공 시 여기에 Authentication 을 넣어줘야함.
 * Authentication 에는 UserDetails 라는 유저 정보를 또 넣어줘야함.
 * Security Session(Authentication(UserDetails)) <-- 이런 식으로 포맷을 직접 맞춰줘야한다.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/security-login")
public class SecurityLoginController {

    private final UserService userService;

    @GetMapping(value = {"", "/"})
    public String home(Model model, Authentication auth) {
        model.addAttribute("loginType", "security-login");
        model.addAttribute("pageName", "security 로그인");

        if (auth != null) {
            User loginUser = userService.getLoginUserByLoginId(auth.getName());
            if (loginUser != null) {
                model.addAttribute("nickname", loginUser.getNickname());
            }
        }

        return "home";
    }


    @GetMapping("/join")
    public String joinPage(Model model) {
        model.addAttribute("loginType", "security-login");
        model.addAttribute("pageName", "security 로그인");

        model.addAttribute("joinRequest", new UserRequestDTO.JoinRequestDTO());
        return "join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute("joinRequest") UserRequestDTO.JoinRequestDTO joinRequest, BindingResult bindingResult, Model model) {
        model.addAttribute("loginType", "security-login");
        model.addAttribute("pageName", "security 로그인");

        if (userService.checkLoginIdDuplicate(joinRequest.getLoginId())) {
            bindingResult.addError(new FieldError("joinRequest", "loginId", "로그인 아이디가 중복됩니다."));
        }

        if (userService.checkNicknameDuplicate(joinRequest.getNickname())) {
            bindingResult.addError(new FieldError("joinRequest", "nickname", "닉네임이 중복됩니다."));
        }

        if (!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())) {
            bindingResult.addError(new FieldError("joinRequest", "passwordCheck", "비밀번호가 일치하지 않습니다."));
        }

        if (bindingResult.hasErrors()) {
            return "join";
        }

        userService.join2(joinRequest);

        return "redirect:/security-login";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginType", "security-login");
        model.addAttribute("pageName", "security 로그인");

        model.addAttribute("loginRequest", new UserRequestDTO.LoginRequestDTO());

        return "login";
    }

    @GetMapping("info")
    public String userInfo(Model model, Authentication auth) {
        model.addAttribute("loginType", "security-login");
        model.addAttribute("pageName", "security 로그인");

        User loginUser = userService.getLoginUserByLoginId(auth.getName());

        if (loginUser == null) {
            return "redirect:/security-login/login";
        }

        model.addAttribute("user", loginUser);
        return "info";
    }

    @GetMapping("/admin")
    public String adminPage(Model model) {
        model.addAttribute("loginType", "security-login");
        model.addAttribute("pageName", "security 로그인");

        return "admin";
    }
}
