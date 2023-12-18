package com.example.web.dto.userDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserRequestDTO {

    @Getter
    @NoArgsConstructor
    public static class JoinRequestDTO {

        @NotBlank(message = "로그인 아이디가 비어있습니다.")
        private String loginId;

        @NotBlank(message = "비밀번호가 비어있습니다.")
        private String password;

        @NotBlank(message = "닉네임이 비어있습니다.")
        private String nickname;
    }

    @Getter
    @NoArgsConstructor
    public static class LoginRequestDTO{
        private String loginId;
        private String password;
    }
}
