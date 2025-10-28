package com.monew.monew_api.domain.interest.service;

import com.monew.monew_api.domain.interest.dto.request.CursorPageRequestInterestDto;
import com.monew.monew_api.domain.interest.dto.request.InterestRegisterRequest;
import com.monew.monew_api.domain.interest.dto.request.InterestUpdateRequest;
import com.monew.monew_api.domain.interest.dto.response.CursorPageResponseInterestDto;
import com.monew.monew_api.domain.interest.dto.response.InterestDto;;

public interface InterestService {

  InterestDto createInterest(InterestRegisterRequest request);
  CursorPageResponseInterestDto getInterests(Long userId, CursorPageRequestInterestDto cursorRequest);
  InterestDto updateInterestKeywords(InterestUpdateRequest request, Long interestId);
  void deleteInterest(Long interestId);
  // 구독 관련 메서드 추후에 구현 예정!!!
  //  구독
  //- 사용자는 관심사를 구독할 수 있습니다.
  //- 구독한 관심사와 관련된 뉴스 기사가 등록되면 알림을 받을 수 있습니다.
}
