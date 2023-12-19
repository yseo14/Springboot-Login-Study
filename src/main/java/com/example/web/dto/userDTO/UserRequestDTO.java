package com.example.web.dto.userDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class UserRequestDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class JoinRequestDTO {

        @NotBlank(message = "로그인 아이디가 비어있습니다.")
        private String loginId;

        @NotBlank(message = "비밀번호가 비어있습니다.")
        private String password;
        private String passwordCheck;

        @NotBlank(message = "닉네임이 비어있습니다.")
        private String nickname;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class LoginRequestDTO{
        private String loginId;
        private String password;
    }
}
