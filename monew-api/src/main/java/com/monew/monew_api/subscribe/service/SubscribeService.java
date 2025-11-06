package com.monew.monew_api.subscribe.service;

import com.monew.monew_api.subscribe.dto.SubscribeDto;

public interface SubscribeService {

  SubscribeDto createSubscribe(Long interestId, Long userId);

  void deleteSubscribe(Long interestId, Long userId);

}
