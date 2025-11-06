package com.monew.monew_api.interest.service;

import com.monew.monew_api.interest.dto.request.CursorPageRequestInterestDto;
import com.monew.monew_api.interest.dto.request.InterestRegisterRequest;
import com.monew.monew_api.interest.dto.request.InterestUpdateRequest;
import com.monew.monew_api.interest.dto.response.CursorPageResponseInterestDto;
import com.monew.monew_api.interest.dto.response.InterestDto;

public interface InterestService {

  InterestDto createInterest(InterestRegisterRequest request);

  CursorPageResponseInterestDto getInterests(Long userId,
      CursorPageRequestInterestDto cursorRequest);

  InterestDto updateInterestKeywords(InterestUpdateRequest request, Long interestId);

  void deleteInterest(Long interestId);

}
