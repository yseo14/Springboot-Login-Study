package com.example.service;

import com.example.converter.UserConverter;
import com.example.domain.User;
import com.example.repository.UserRepository;
import com.example.web.dto.userDTO.UserRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Spring Security를 사용한 로그인 구현 시 사용
    // private final BCryptPasswordEncoder encoder;

    /**
     * loginId 중복 체크
     * 회원가입 기능 구현 시 사용
     * 중복되면 true return
     */
    public boolean checkLoginIdDuplicate(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    /**
     * nickname 중복 체크
     * 회원가입 기능 구현 시 사용
     *
     * @param nickname 중복되면 true return
     */
    public boolean checkNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    /**
     * 회원가입 기능1 (암호화 x)
     * 화면에서 JoinRequest(loginId, password, nickname)을 입력 받아 User로 변환 후 저장
     * loginId, nickname 중복 체크는 controller에서 -> 에러메시지 출력을 위해
     *
     * @param request
     */
    public User join(UserRequestDTO.JoinRequestDTO request) {

        return userRepository.save(UserConverter.toUser(request));
    }

    /**
     * 회원가입 기능2 (암호화 o)
     * 화면에서 JoinRequest(loginId, password, nickname)을 입력 받아 User로 변환 후 저장
     * 회원가입 기능1 과 달리 비밀번호를 암호화한다.
     * loginId, nickname 중복 체크는 controller에서 -> 에러메시지 출력을 위해
     *
     * @param request
     */
    public User join2(UserRequestDTO.JoinRequestDTO request) {

        return userRepository.save(UserConverter.toUser(request, request.getPassword()));
    }


    /**
     * 로그인 기능
     * 화면에서 LoginRequest(loginId, password)을 입력받아 loginId와 password가 일치하면 User return
     *
     * @param request
     * @return
     */
    public User login(UserRequestDTO.LoginRequestDTO request) {
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        // loginId로 찾은 유저의 password와 입력된 password가 다를 경우 null return
        if (!user.getPassword().equals(request.getPassword())) {
            return null;
        }

        return user;
    }

    /**
     * userId(Long)를 입력받아 User을 return 해주는 기능
     * 인증, 인가 시 사용
     * userId가 null이거나(로그인 X) userId로 찾아온 User가 없으면 null return
     * userId로 찾아온 User가 존재하면 User return
     */
    public User getLoginUserById(Long userId) {
        if (userId == null)
            return null;

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        return user;
    }

    /**
     * loginId(String)를 입력받아 User을 return 해주는 기능
     * 인증, 인가 시 사용
     * loginId가 null이거나(로그인 X) userId로 찾아온 User가 없으면 null return
     * loginId로 찾아온 User가 존재하면 User return
     */
    public User getLoginUserByLoginId(String loginId) {
        if (loginId == null)
            return null;

        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        return user;
    }

}
