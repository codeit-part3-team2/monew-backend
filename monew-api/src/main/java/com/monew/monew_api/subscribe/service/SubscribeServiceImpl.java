package com.monew.monew_api.subscribe.service;

import com.monew.monew_api.common.exception.interest.InterestNotFoundException;
import com.monew.monew_api.common.exception.subscribe.SubscribeDuplicateException;
import com.monew.monew_api.common.exception.subscribe.SubscribeNotFoundException;
import com.monew.monew_api.common.exception.user.UserNotFoundException;
import com.monew.monew_api.domain.user.User;
import com.monew.monew_api.domain.user.repository.UserRepository;
import com.monew.monew_api.interest.entity.Interest;
import com.monew.monew_api.interest.repository.InterestRepository;
import com.monew.monew_api.subscribe.dto.SubscribeDto;
import com.monew.monew_api.subscribe.entity.Subscribe;
import com.monew.monew_api.subscribe.mapper.SubscribeMapper;
import com.monew.monew_api.subscribe.repository.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscribeServiceImpl implements SubscribeService {

  private final InterestRepository interestRepository;
  private final UserRepository userRepository;
  private final SubscribeRepository subscribeRepository;

  private final SubscribeMapper subscribeMapper;

  @Override
  @Transactional
  public SubscribeDto createSubscribe(Long interestId, Long userId) {

    Interest interest = interestRepository.findById(interestId)
        .orElseThrow(InterestNotFoundException::new);
    User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

    if(subscribeRepository.existsByInterestAndUser(interest, user)){
      throw new SubscribeDuplicateException();
    }
    log.info("현재 관심사 구독자 수 : {}", interest.getSubscriberCount());
    interest.addSubscriberCount();
    log.info("관심사 구독 후 구독자 수: {}", interest.getSubscriberCount());

    Subscribe subscribe = Subscribe.create(interest, user);
    Subscribe saved = subscribeRepository.save(subscribe);

    return subscribeMapper.toSubscribeDto(saved);
  }

  @Override
  @Transactional
  public void deleteSubscribe(Long interestId, Long userId) {

    Interest interest = interestRepository.findById(interestId)
        .orElseThrow(InterestNotFoundException::new);
    User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

    Subscribe subscribe = subscribeRepository.findByInterestAndUser(interest,user)
        .orElseThrow(SubscribeNotFoundException::new);

    subscribeRepository.delete(subscribe);
    log.info("현재 관심사 구독자 수 : {}", interest.getSubscriberCount());
    interest.cancelSubscriberCount();
    log.info("관심사 구독 취소 후 구독자 수: {}", interest.getSubscriberCount());
  }
}
