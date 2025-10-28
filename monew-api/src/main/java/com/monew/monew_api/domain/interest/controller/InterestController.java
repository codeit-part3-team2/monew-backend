package com.monew.monew_api.domain.interest.controller;

import com.monew.monew_api.domain.interest.dto.request.CursorPageRequestInterestDto;
import com.monew.monew_api.domain.interest.dto.request.InterestRegisterRequest;
import com.monew.monew_api.domain.interest.dto.request.InterestUpdateRequest;
import com.monew.monew_api.domain.interest.dto.response.CursorPageResponseInterestDto;
import com.monew.monew_api.domain.interest.dto.response.InterestDto;
import com.monew.monew_api.domain.interest.service.InterestService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interests")
@RequiredArgsConstructor
@Slf4j
public class InterestController {

  private final InterestService interestService;

  @PostMapping
  public ResponseEntity<InterestDto> createInterest(
      @RequestBody @Valid InterestRegisterRequest request){

    log.info("관심사 등록 요청: {}", request);
    InterestDto response = interestService.createInterest(request);
    log.info("관심사 등록 완료: {}", response);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }


  @GetMapping
  public ResponseEntity<CursorPageResponseInterestDto> getInterests(
      @RequestHeader("Monew-Request-User-Id") Long userId,
      @ParameterObject @ModelAttribute CursorPageRequestInterestDto request
    ) {

    log.info("관심사 조회 요청: {}", request);
    CursorPageResponseInterestDto response = interestService.getInterests(userId,request);
    log.info("관심사 조회 완료: {}", request);

    return ResponseEntity.ok(response);
  }


  @DeleteMapping("/{interestId}")
  public ResponseEntity<Void> deleteInterest(
      @PathVariable Long interestId
  ){
    log.info("관심사 삭제 요청 : interestId = {}", interestId);
    interestService.deleteInterest(interestId);
    log.info("관심사 삭제 완료 : interestId = {}", interestId);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }


  @PatchMapping("/{interestId}")
  public ResponseEntity<InterestDto> updateInterestKeywords(
      @PathVariable Long interestId,
      @RequestBody InterestUpdateRequest request
  ){
    log.info("관심사 키워드 수정 요청 : interestId={}, request={}", interestId, request);
    InterestDto updatedKeyword = interestService
        .updateInterestKeywords(request, interestId);
    log.info("관심사 키워드 수정 완료 : response={}", updatedKeyword);

    return ResponseEntity.status(HttpStatus.OK).body(updatedKeyword);
  }


}
