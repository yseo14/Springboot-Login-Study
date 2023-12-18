package com.example.converter;

import com.example.domain.User;
import com.example.domain.enums.UserRole;
import com.example.web.dto.userDTO.UserRequestDTO;

public class UserConverter {

    //암호화 X
    public static User toUser(UserRequestDTO.JoinRequestDTO request) {
        return User.builder()
                .loginId(request.getLoginId())
                .password(request.getPassword())
                .nickname(request.getNickname())
                .role(UserRole.USER)
                .build();
    }

    //암호화 O
    public static User toUser(UserRequestDTO.JoinRequestDTO request, String encodePassword) {
        return User.builder()
                .loginId(request.getLoginId())
                .password(encodePassword)
                .nickname(request.getNickname())
                .role(UserRole.USER)
                .build();
    }
}
