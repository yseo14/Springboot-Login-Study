package com.example.web.controller;

import com.example.domain.User;
import com.example.domain.enums.UserRole;
import com.example.service.UserService;
import com.example.web.dto.userDTO.UserRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.FieldError;

/**
 *  세션: 일정 시간동안 같은 사용자로부터 들어오는 일련의 요구를 하나의 상태로 보고 그 상태를 일정하게 유지시키는 기술.
 *       쿠키와 달리 사용자의 정보를 사용자의 메모리가 아닌 서버측에 저장한다.
 *
 *  로그인 성공 시 세션을 하나 생성
 *  세션의 key 값은 UUID(중복되지 않는 랜덤 값, 예측 불가)로 설정한다.
 *  value 값에 사용자 정보를 넣는다. 생성된 세션은 서버측 세션 저장소에 보관된다. 세션의 key 값을 쿠키를 통해 사용자에게 전달한다.
 *  사용자는 로그인 성공 이후 다른 요청을 할 때마다 이 쿠키를 서버에 같이 전송한다.
 *  서버측에서 사용자에게 쿠키를 통해 UUID 값을 받으면, 전달 받은 UUID를 Key값으로 가지는 세션을 서버측에서 세션 저장소에서 검색
 */


@Controller
@RequiredArgsConstructor
@RequestMapping("/session-login")
public class SessionLoginController {

    private final UserService userService;

    @GetMapping(value = {"", "/"})
    public String home(Model model, @SessionAttribute(name = "userId", required = false) Long userId) {
        model.addAttribute("loginType", "session-login");
        model.addAttribute("pageName", "세션 로그인");

        User loginUser = userService.getLoginUserById(userId);

        if (loginUser != null) {
            model.addAttribute("nickname", loginUser.getNickname());
        }

        return "home";
    }

    @GetMapping("/join")
    public String joinPage(Model model) {
        model.addAttribute("loginType", "session-login");
        model.addAttribute("pageName", "세션 로그인");

        model.addAttribute("joinRequest", new UserRequestDTO.JoinRequestDTO());

        return "join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute UserRequestDTO.JoinRequestDTO joinRequest, BindingResult bindingResult, Model model) {
        model.addAttribute("loginType", "session-login");
        model.addAttribute("pageName", "세션 로그인");

        // loginId 중복 체크
        if (userService.checkLoginIdDuplicate(joinRequest.getLoginId())) {
            bindingResult.addError(new FieldError("joinRequest", "loginId", "로그인 아이디가 중복됩니다."));
        }
        // 닉네임 중복 체크
        if (userService.checkNicknameDuplicate(joinRequest.getNickname())) {
            bindingResult.addError(new FieldError("joinRequest", "nickname", "닉네임이 중복됩니다."));
        }
        // password와 passwordCheck가 같은지 체크
        if (!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())) {
            bindingResult.addError(new FieldError("joinRequest", "passwordCheck", "바밀번호가 일치하지 않습니다."));
        }

        if (bindingResult.hasErrors()) {
            return "join";
        }

        userService.join(joinRequest);
        return "redirect:/session-login";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginType", "session-login");
        model.addAttribute("pageName", "세션 로그인");

        model.addAttribute("loginRequest", new UserRequestDTO.LoginRequestDTO());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute UserRequestDTO.LoginRequestDTO loginRequest, BindingResult bindingResult,
                        HttpServletRequest httpServletRequest, Model model) {
        model.addAttribute("loginType", "session-login");
        model.addAttribute("pageName", "세션 로그인");

        User user = userService.login(loginRequest);

        // 로그인 아이디나 비밀번호가 틀린 경우 global error return
        if (user == null) {
            bindingResult.reject("loginFail", "로그인 아이디 또는 비밀번호가 틀렸습니다.");
        }

        if (bindingResult.hasErrors()) {
            return "login";
        }

        // 로그인 성공 => 세션 생성

        // 세션을 생성하기 전에 기존의 세션 파기
        httpServletRequest.getSession().invalidate();
        HttpSession session = httpServletRequest.getSession(true);  // Session이 없으면 생성
        // 세션에 userId를 넣어줌
        session.setAttribute("userId", user.getId());
        session.setMaxInactiveInterval(1800); // Session이 30분동안 유지

        return "redirect:/session-login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, Model model) {
        model.addAttribute("loginType", "session-login");
        model.addAttribute("pageName", "세션 로그인");

        HttpSession session = request.getSession(false);    //session이 이미 만료되어 없으면 false 리턴
        if (session != null) {  // session이 존재할 경우 로그아웃을 해야하니까 기존의 session을 파기한다.
            session.invalidate();
        }

        return "redirect:/session-login";
    }

    @GetMapping("/info")
    public String userInfo(@SessionAttribute(name = "userId", required = false) Long userId, Model model) {
        model.addAttribute("loginType", "session-login");
        model.addAttribute("pageName", "세션 로그인");

        User loginUser = userService.getLoginUserById(userId);

        if(loginUser == null) {
            return "redirect:/session-login/login";
        }

        model.addAttribute("user", loginUser);
        return "info";
    }

    @GetMapping("/admin")
    public String adminPage(@SessionAttribute(name = "userId", required = false) Long userId, Model model) {
        model.addAttribute("loginType", "session-login");
        model.addAttribute("pageName", "세션 로그인");

        User loginUser = userService.getLoginUserById(userId);

        if(loginUser == null) {
            return "redirect:/session-login/login";
        }

        if(!loginUser.getRole().equals(UserRole.ADMIN)) {
            return "redirect:/session-login";
        }

        return "admin";
    }
}
