package com.monew.monew_api.domain.interest.service;

import com.monew.monew_api.common.exception.interest.InterestDuplicatedException;
import com.monew.monew_api.common.exception.interest.InterestNotFoundException;
import com.monew.monew_api.common.exception.user.UserNotFoundException;
import com.monew.monew_api.domain.interest.dto.InterestSortBy;
import com.monew.monew_api.domain.interest.dto.request.CursorPageRequestInterestDto;
import com.monew.monew_api.domain.interest.dto.request.InterestRegisterRequest;
import com.monew.monew_api.domain.interest.dto.request.InterestUpdateRequest;
import com.monew.monew_api.domain.interest.dto.response.CursorPageResponseInterestDto;
import com.monew.monew_api.domain.interest.dto.response.InterestDto;
import com.monew.monew_api.domain.interest.entity.Interest;
import com.monew.monew_api.domain.interest.entity.InterestKeyword;
import com.monew.monew_api.domain.interest.entity.Keyword;
import com.monew.monew_api.domain.interest.mapper.InterestMapper;
import com.monew.monew_api.domain.interest.repository.InterestRepository;
import com.monew.monew_api.domain.interest.repository.KeywordRepository;
import com.monew.monew_api.domain.user.repository.UserRepository;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterestServiceImpl implements InterestService {

  private final InterestRepository interestRepository;
  private final UserRepository userRepository;
  private final KeywordRepository keywordRepository;
  private final InterestMapper interestMapper;

  @Override
  @Transactional
  public InterestDto createInterest(InterestRegisterRequest request) {

    log.info("새로운 관심사 등록 요청: {}", request);
    String interestName = request.name();

    // 유사도 검사
    String similarName = findSimilarInterestName(interestName);
    if(similarName != null) {
      Map<String, Object> details = new HashMap<>();
      details.put("name", similarName);
      log.warn("유사한 관심사 이름: {}", similarName);
      throw new InterestDuplicatedException(details);
    }

    Interest interest = Interest.create(interestName);

    // 키워드 저장
    Set<String> keywordSet = new HashSet<>(request.keywords());
    for(String keyword : keywordSet) {
      Keyword getKeyword = keywordRepository.findByKeyword(keyword)
          .orElseGet(() -> keywordRepository.save(new Keyword(keyword)));
      interest.addKeyword(getKeyword);
    }

    Interest savedInterest = interestRepository.save(interest);

    List<String> keywords = savedInterest.getKeywords().stream()
        .map(ik -> ik.getKeyword().getKeyword())
        .collect(Collectors.toList());

    InterestDto response = interestMapper.toInterestDto(savedInterest, keywords, false);
    log.info("관심사 등록 완료: {}", response);

    return response;
  }


  @Override
  @Transactional(readOnly = true)
  public CursorPageResponseInterestDto<InterestDto> getInterests(Long userId,
      CursorPageRequestInterestDto request) {

    log.info("관심사 조회 요청 : {}", request);
    userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

    final String keyword = (request.keyword() == null) ? null : request.keyword();
    final InterestSortBy sortBy = request.sortBy();
    final Direction direction = request.direction();
    final String cursor = request.cursor();
    final LocalDateTime after = request.after();
    final int limit = request.limit();

    // 페이지 조회
    Slice<Interest> slices = interestRepository.findAll(
        keyword, sortBy, direction, cursor, after, limit);

    List<Interest> interests = slices.getContent();

    Set<Long> interestIds = interests.stream().map(Interest::getId).collect(Collectors.toSet());

    List<InterestDto> interestDtos = new ArrayList<>(interests.size());
    for (Interest interest : interests) {
      List<String> keywords = new ArrayList<>();
      for(InterestKeyword ik : interest.getKeywords()) {
        String name = ik.getKeyword().getKeyword();
        keywords.add(name);
      }
      // ⭐️⭐️ 구독 여부 확인 조회 코드 필요!!!!
      interestDtos.add(interestMapper.toInterestDto(interest,keywords,false));
    }

    Long totalElements = interestRepository.countFilteredTotalElements(keyword, sortBy,direction);

    boolean hasNext = slices.hasNext();
    String nextCursor = null;
    LocalDateTime nextAfter = null;


    if(hasNext && !interestIds.isEmpty()) {
      Interest last = interests.get(interests.size() - 1);
      if(sortBy == InterestSortBy.NAME){
        nextCursor = last.getName();
      } else if (sortBy == InterestSortBy.SUBSCRIBER_COUNT){
        nextCursor = String.valueOf(last.getSubscriberCount());
      }
      nextAfter = last.getCreatedAt();
    }

    return new CursorPageResponseInterestDto<>(
        interestDtos, nextCursor, nextAfter, interestDtos.size(), totalElements, hasNext);
  }


  @Override
  @Transactional
  public InterestDto updateInterestKeywords(
      InterestUpdateRequest request, Long interestId, Long userId) {

    userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    Interest interest =  interestRepository.findById(interestId).orElseThrow(InterestNotFoundException::new);

    updateKeywords(interest, request.keywords());

    List<String> keywords = interest.getKeywords().stream()
        .map(ik -> ik.getKeyword().getKeyword())
        .collect(Collectors.toList());

    // ⭐️⭐️구독 여부 가져오는 코드 추가 필요!!
    return interestMapper.toInterestDto(interest, keywords, false);
  }


  @Override
  @Transactional
  public void deleteInterest(Long interestId) {
    Interest interest = interestRepository.findById(interestId)
        .orElseThrow(InterestNotFoundException::new);
    interestRepository.delete(interest);
  }


  private String findSimilarInterestName(String newInterestName) {
    for (Interest existingInterest : interestRepository.findAll()) {
      double similarity = calculateSimilarity(existingInterest.getName(), newInterestName);
      if (similarity >= 0.8) {
        return existingInterest.getName();
      }
    }
    return null;
  }


  private double calculateSimilarity(String name1, String name2) {
    if(name1 == null || name2 == null) {
      return 0.0;
    }
    LevenshteinDistance levenshtein = LevenshteinDistance.getDefaultInstance();
    int distance = levenshtein.apply(name1, name2);
    int maxLength = Math.max(name1.length(), name2.length());
    double similarity = 1.0 - ((double) distance / maxLength);
    return similarity;
  }


  private void updateKeywords(
      Interest interest, @Size(min = 1, max = 10) List<String> requestKeywords) {

    Map<String, InterestKeyword> savedKeywords = interest.getKeywords().stream()
        .collect(Collectors.toMap(
            ik -> ik.getKeyword().getKeyword(),
            ik -> ik));

    Set<String> requestKeywordSet = new HashSet<>(requestKeywords);

    List<Keyword> existingKeywords = keywordRepository.findAllByKeywordIn(requestKeywordSet);
    Map<String, Keyword> existingKeywordMap = existingKeywords.stream()
        .collect(Collectors.toMap(Keyword::getKeyword, k -> k));

    for (String keyword : requestKeywordSet) {
      if (!savedKeywords.containsKey(keyword)) {
        Keyword getKeyword = existingKeywordMap.getOrDefault(keyword, new Keyword(keyword));
        if (getKeyword.getId() == null) {
          getKeyword = keywordRepository.save(getKeyword);
        }
        interest.addKeyword(getKeyword);
      } else {
        savedKeywords.remove(keyword);
      }
    }
    removeOrphanKeywords(interest, savedKeywords);
  }


  private void removeOrphanKeywords(Interest interest, Map<String, InterestKeyword> toRemove) {
    if (toRemove.isEmpty()) {return;}
    List<Keyword> removedKeyword = new ArrayList<>();

    for (InterestKeyword interestKeyword : toRemove.values()) {
      interest.getKeywords().remove(interestKeyword);
      removedKeyword.add(interestKeyword.getKeyword());
    }

    List<Keyword> toDelete = keywordRepository.findOrphanKeywordsIn(removedKeyword);
    keywordRepository.deleteAll(toDelete);
  }
}
