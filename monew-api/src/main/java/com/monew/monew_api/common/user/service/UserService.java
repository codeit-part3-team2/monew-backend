package com.monew.monew_api.common.user.service;

import com.monew.monew_api.common.exception.user.UserEmailDuplicateException;
import com.monew.monew_api.common.exception.user.UserNotFoundException;
import com.monew.monew_api.common.exception.user.UserUnauthorizedException;
import com.monew.monew_api.common.user.User;
import com.monew.monew_api.common.user.dto.*;
import com.monew.monew_api.common.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto signup(UserRegisterRequest request) {
        log.info("[회원가입 시도] 이메일: {}, 닉네임: {}", request.getEmail(), request.getNickname());

        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("[회원가입 실패] 이메일 중복: {}", request.getEmail());
            throw new UserEmailDuplicateException();
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 사용자 생성
        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(encodedPassword)
                .build();

        User savedUser = userRepository.save(user);
        log.info("[회원가입 성공] 사용자 ID: {}, 이메일: {}", savedUser.getId(), savedUser.getEmail());

        return UserDto.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .nickname(savedUser.getNickname())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }

    public UserDto login(UserLoginRequest request) {
        log.info("[로그인 시도] 이메일: {}", request.getEmail());

        // 이메일로 사용자 찾기 (논리삭제되지 않은 사용자만)
        User user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("[로그인 실패] 존재하지 않는 사용자: {}", request.getEmail());
                    return new UserUnauthorizedException();
                });

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("[로그인 실패] 비밀번호 불일치: {}", request.getEmail());
            throw new UserUnauthorizedException();
        }

        log.info("[로그인 성공] 사용자 ID: {}, 이메일: {}", user.getId(), user.getEmail());

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Transactional
    public UserDto updateUser(Long userId, UserUpdateRequest request) {
        log.info("[사용자 정보 수정 시도] 사용자 ID: {}", userId);

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> {
                    log.warn("[사용자 정보 수정 실패] 사용자를 찾을 수 없음: {}", userId);
                    return new UserNotFoundException();
                });

        // 닉네임 업데이트
        if (request.hasNickname()) {
            log.debug("[닉네임 변경] 사용자 ID: {}, 변경 전: {}, 변경 후: {}",
                    userId, user.getNickname(), request.getNickname());
            user.updateNickname(request.getNickname());
        }

        log.info("[사용자 정보 수정 성공] 사용자 ID: {}", userId);

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Transactional
    public void softDeleteUser(Long userId) {
        log.info("[사용자 삭제 시도] 사용자 ID: {}", userId);

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> {
                    log.warn("[사용자 삭제 실패] 사용자를 찾을 수 없음: {}", userId);
                    return new UserNotFoundException();
                });

        user.softDelete();
        log.info("[사용자 삭제 성공] 사용자 ID: {}, 이메일: {}", userId, user.getEmail());
    }

    @Transactional
    public void hardDeleteUser(Long userId) {
        log.info("[사용자 영구 삭제 시도] 사용자 ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("[사용자 영구 삭제 실패] 사용자를 찾을 수 없음: {}", userId);
                    return new UserNotFoundException();
                });

        userRepository.delete(user);
        log.warn("[사용자 영구 삭제 완료] 사용자 ID: {}, 이메일: {}", userId, user.getEmail());
    }
}
