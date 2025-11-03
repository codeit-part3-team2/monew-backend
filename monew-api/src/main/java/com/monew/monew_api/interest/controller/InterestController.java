package com.monew.monew_api.interest.controller;

import com.monew.monew_api.interest.dto.request.CursorPageRequestInterestDto;
import com.monew.monew_api.interest.dto.request.InterestRegisterRequest;
import com.monew.monew_api.interest.dto.request.InterestUpdateRequest;
import com.monew.monew_api.interest.dto.response.CursorPageResponseInterestDto;
import com.monew.monew_api.interest.dto.response.InterestDto;
import com.monew.monew_api.interest.service.InterestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interests")
@RequiredArgsConstructor
@Slf4j
public class InterestController {

  private final InterestService interestService;

  @PostMapping
  public ResponseEntity<InterestDto> createInterest(
      @RequestBody @Valid InterestRegisterRequest request
  ) {
    log.info("[API 요청] POST/api/interests/ - 관심사 등록 요청 : {}", request);
    InterestDto response = interestService.createInterest(request);
    log.info("[API 응답] POST/api/interests/ - 관심사 등록 응답 : {}", response);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }


  @GetMapping
  public ResponseEntity<CursorPageResponseInterestDto> getInterests(
      @RequestHeader("Monew-Request-User-Id") Long userId,
      @ParameterObject @ModelAttribute CursorPageRequestInterestDto request
  ) {
    log.info("[API 요청] GET/api/interests/ - 관심사 조회 요청 : {}", request);
    CursorPageResponseInterestDto response = interestService.getInterests(userId, request);
    log.info("[API 응답] GET/api/interests/ - 관심사 조회 응답 : {}", response);
    return ResponseEntity.ok(response);
  }


  @DeleteMapping("/{interestId}")
  public ResponseEntity<Void> deleteInterest(
      @PathVariable Long interestId
  ) {
    log.info("[API 요청] DELETE/api/interests/{} - 관심사 삭제 요청", interestId);
    interestService.deleteInterest(interestId);
    log.info("[API 응답] DELETE/api/interests/{} - 관심사 삭제 응답", interestId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }


  @PatchMapping("/{interestId}")
  public ResponseEntity<InterestDto> updateInterestKeywords(
      @PathVariable Long interestId,
      @RequestBody @Valid InterestUpdateRequest request
  ) {
    log.info("[API 요청] PATCH/api/interests/{} - 관심사 키워드 수정 요청 : {}", interestId, request);
    InterestDto response = interestService
        .updateInterestKeywords(request, interestId);
    log.info("[API 응답] PATCH/api/interests/{} - 관심사 키워드 수정 응답 : {}", interestId, response);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }


}
