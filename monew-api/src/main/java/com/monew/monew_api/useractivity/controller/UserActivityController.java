package com.monew.monew_api.useractivity.controller;

import com.monew.monew_api.useractivity.dto.UserActivityDto;
import com.monew.monew_api.useractivity.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/user-activities")
@RequiredArgsConstructor
public class UserActivityController {

    private final UserActivityService userActivityService;


    /*
     mongoDB 사용 시 getUserActivityWithCache 메서드
     단일 쿼리 사용시 getUserActivitySingleQuery 메서드 (네이티브 쿼리)
     여러 쿼리 사용시 getUserActivity 메서드
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserActivityDto> getUserActivity(@PathVariable String userId) {
        log.info("활동내역 조회 요청: userId={}", userId);

        UserActivityDto activity = userActivityService.getUserActivitySingleQuery(userId);

        return ResponseEntity.ok(activity);
    }
}