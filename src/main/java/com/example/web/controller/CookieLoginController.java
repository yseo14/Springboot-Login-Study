package com.example.web.controller;

import com.example.domain.User;
import com.example.domain.enums.UserRole;
import com.example.service.UserService;
import com.example.web.dto.userDTO.UserRequestDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

/**
 * 쿠키: 사용자가 웹사이트에 접속 시 사용자의 개인 장치에 다운로드되고, 브라우저에 저장되는 작은 텍스트 파일
 *
 *  로그인 성공 시 서버가 쿠키에 사용자 정보를 넣어주고,
 *  클라이언트 측에서는 다음 요청을 할 때마다 이 쿠키를 서버에 같이 보내준다.
 *  서버에서는 이 쿠키를 통해 로그인을 했는지, 유저 정보, 권한 등을 확인할 수 있다.
 *
 *  new Cookie() 를 이용해 쿠키를 생성하고, Key, Value 값을 넣어줄 수 있다.
 *  setMaxAge() 메서드를 이용해 쿠키의 유효시간을 설정할 수 있다.
 *  이렇게 만들어진 쿠키를 HttpServletResponse 객체에 addCookie를 통해 쿠키를 태워서 전송한다.
 */

@Controller
@RequiredArgsConstructor
@RequestMapping("/cookie-login")
public class CookieLoginController {

    private final UserService userService;

    @GetMapping(value = {"", "/"})
    public String home(@CookieValue(name = "userId", required = false) Long userId, Model model) {  //애노테이션의 required 속성을 true로 지정 시, value 속성의 이름을 가진 쿠키가 존재하지 않을 경우 예외 발생시킴.
        model.addAttribute("loginType", "cookie-login");
        model.addAttribute("pageName", "쿠키 로그인");

        User loginUser = userService.getLoginUserById(userId);

        if (loginUser != null) {
            model.addAttribute("nickname", loginUser.getNickname());
        }
        return "home";
    }

    @GetMapping("/join")
    public String joinPage(Model model) {
        model.addAttribute("loginType", "cookie-login");
        model.addAttribute("pageName", "쿠키 로그인");

        model.addAttribute("joinRequest", new UserRequestDTO.JoinRequestDTO());

        return "join";
    }

    @PostMapping(value = "/join")
    public String join(@Valid @ModelAttribute UserRequestDTO.JoinRequestDTO joinRequest, BindingResult bindingResult, Model model) {
        model.addAttribute("loginType", "cookie-login");
        model.addAttribute("pageName", "쿠키 로그인");

        if (userService.checkLoginIdDuplicate(joinRequest.getLoginId())) {
            bindingResult.addError(new FieldError("joinRequest", "loginId", "로그인 아이디가 중복됩니다."));
        }
        if (userService.checkNicknameDuplicate(joinRequest.getNickname())) {
            bindingResult.addError(new FieldError("joinRequest", "nickname", "로그인 닉네임이 중복됩니다."));
        }
        if (!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())) {
            bindingResult.addError(new FieldError("joinRequest", "passwordCheck", "비밀번호가 일치하지 않습니다."));
        }

        if (bindingResult.hasErrors()) {
            return "join";
        }

        userService.join(joinRequest);
        return "redirect:/cookie-login";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginType", "cookie-login");
        model.addAttribute("pageName", "쿠키 로그인");
        model.addAttribute("loginRequest", new UserRequestDTO.LoginRequestDTO());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute UserRequestDTO.LoginRequestDTO loginRequest, BindingResult bindingResult,
                        HttpServletResponse response, Model model) {
        model.addAttribute("loginType", "cookie-login");
        model.addAttribute("pageName", "쿠키 로그인");

        User user = userService.login(loginRequest);

        if (user == null) {
            bindingResult.reject("loginFail", " 로그인 아이디 또는 비밀번호가 틀렸습니다.");
        }

        if (bindingResult.hasErrors()) {
            return "login";
        }

        // 로그인 성공 -> 쿠키 생성
        Cookie cookie = new Cookie("userId", String.valueOf(user.getId()));
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);

        return "redirect:/cookie-login";
    }

    @GetMapping(value = "/logout")
    public String logout(HttpServletResponse response, Model model) {
        model.addAttribute("loginType", "cookie-login");
        model.addAttribute("pageName", "쿠키 로그인");

        //쿠키 파기
        Cookie cookie = new Cookie("userId", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "redirect:/cookie-login";
    }

    @GetMapping("/info")
    public String userInfo(@CookieValue(name = "userId", required = false) Long userId, Model model) {
        model.addAttribute("loginType", "cookie-login");
        model.addAttribute("pageName", "쿠키 로그인");

        User loginUser = userService.getLoginUserById(userId);  //  쿠키에 담긴 유저의 id를 통해 유저를 get

        if (loginUser == null) {
            return "redirect:/cookie-login/login";
        }

        model.addAttribute("user", loginUser);
        return "info";
    }

    @GetMapping("/admin")
    public String adminPage(@CookieValue(name = "userId", required = false) Long userId, Model model) {
        model.addAttribute("loginType", "cookie-login");
        model.addAttribute("pageName", "쿠키 로그인");

        User loginUser = userService.getLoginUserById(userId);

        if (loginUser == null) {
            return "redirect:/cookie-login/login";
        }

        if (!loginUser.getRole().equals(UserRole.ADMIN)) {
            return "redirect:/cookie-login";
        }
        return "admin";
    }
}
