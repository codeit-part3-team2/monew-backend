package com.monew.monew_api.domain.user.controller;

import com.monew.monew_api.domain.user.dto.*;
import com.monew.monew_api.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/users")
    public ResponseEntity<UserDto> signup(@Valid @RequestBody UserRegisterRequest request) {
        log.info("[API 요청] POST /api/users - 회원가입 요청, 이메일: {}", request.getEmail());
        UserDto response = userService.signup(request);
        log.info("[API 응답] POST /api/users - 회원가입 성공, 사용자 ID: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/api/users/login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody UserLoginRequest request) {
        log.info("[API 요청] POST /api/users/login - 로그인 요청, 이메일: {}", request.getEmail());
        UserDto response = userService.login(request);
        log.info("[API 응답] POST /api/users/login - 로그인 성공, 사용자 ID: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/api/users/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateRequest request) {
        log.info("[API 요청] PATCH /api/users/{} - 사용자 정보 수정 요청", userId);
        UserDto response = userService.updateUser(userId, request);
        log.info("[API 응답] PATCH /api/users/{} - 사용자 정보 수정 성공", userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/user/{userId}")
    public ResponseEntity<Void> softDeleteUser(@PathVariable Long userId) {
        log.info("[API 요청] DELETE /api/user/{} - 사용자 삭제 요청", userId);
        userService.softDeleteUser(userId);
        log.info("[API 응답] DELETE /api/user/{} - 사용자 삭제 성공", userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/user/{userId}/hard")
    public ResponseEntity<Void> hardDeleteUser(@PathVariable Long userId) {
        log.info("[API 요청] DELETE /api/user/{}/hard - 사용자 영구 삭제 요청", userId);
        userService.hardDeleteUser(userId);
        log.info("[API 응답] DELETE /api/user/{}/hard - 사용자 영구 삭제 성공", userId);
        return ResponseEntity.noContent().build();
    }
}
