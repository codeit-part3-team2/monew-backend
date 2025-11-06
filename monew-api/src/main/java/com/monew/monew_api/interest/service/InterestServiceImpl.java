package com.monew.monew_api.interest.service;

import com.monew.monew_api.article.repository.ArticleRepository;
import com.monew.monew_api.article.repository.InterestArticleKeywordRepository;
import com.monew.monew_api.article.repository.InterestArticlesRepository;
import com.monew.monew_api.common.exception.interest.InterestDuplicatedException;
import com.monew.monew_api.common.exception.interest.InterestNotFoundException;
import com.monew.monew_api.user.repository.UserRepository;
import com.monew.monew_api.interest.event.InterestDeletedEvent;
import com.monew.monew_api.interest.event.InterestUpdatedEvent;
import com.monew.monew_api.interest.dto.InterestOrderBy;
import com.monew.monew_api.interest.dto.request.CursorPageRequestInterestDto;
import com.monew.monew_api.interest.dto.request.InterestRegisterRequest;
import com.monew.monew_api.interest.dto.request.InterestUpdateRequest;
import com.monew.monew_api.interest.dto.response.CursorPageResponseInterestDto;
import com.monew.monew_api.interest.dto.response.InterestDto;
import com.monew.monew_api.interest.entity.Interest;
import com.monew.monew_api.interest.entity.InterestKeyword;
import com.monew.monew_api.interest.entity.Keyword;
import com.monew.monew_api.interest.mapper.InterestMapper;
import com.monew.monew_api.interest.repository.InterestRepository;
import com.monew.monew_api.interest.repository.KeywordRepository;
import com.monew.monew_api.subscribe.repository.SubscribeRepository;
import com.monew.monew_api.subscribe.repository.SubscribeRepository.InterestCountProjection;
import com.querydsl.core.types.Order;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterestServiceImpl implements InterestService {

  private final InterestRepository interestRepository;
  private final KeywordRepository keywordRepository;
  private final SubscribeRepository subscribeRepository;

  private final ArticleRepository articleRepository;
  private final InterestArticlesRepository interestArticlesRepository;
  private final InterestArticleKeywordRepository interestArticleKeywordRepository;

  private final InterestMapper interestMapper;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  @Transactional
  public InterestDto createInterest(InterestRegisterRequest request) {

    String interestName = request.name();

    // 유사도 검사
    String similarName = findSimilarInterestName(interestName);
    if (similarName != null) {
      Map<String, Object> details = new HashMap<>();
      details.put("name", similarName);
      log.warn("유사한 관심사 이름: {}", similarName);
      throw new InterestDuplicatedException(details);
    }

    Interest interest = Interest.create(interestName);

    // 키워드 저장
    Set<String> keywordSet = new HashSet<>(request.keywords());
    for (String keyword : keywordSet) {
      Keyword getKeyword = keywordRepository.findByKeyword(keyword)
          .orElseGet(() -> keywordRepository.save(new Keyword(keyword)));
      interest.addKeyword(getKeyword);
    }

    Interest savedInterest = interestRepository.save(interest);

    List<String> keywords = savedInterest.getKeywords().stream()
        .map(ik -> ik.getKeyword().getKeyword())
        .collect(Collectors.toList());

    return interestMapper.toDto(savedInterest, keywords, false);
  }

  @Override
  @Transactional(readOnly = true)
  public CursorPageResponseInterestDto getInterests(Long userId,
      CursorPageRequestInterestDto request) {

    final String keyword = (request.keyword() == null || request.keyword().isBlank())
        ? null : request.keyword();
    final InterestOrderBy orderBy =
        (request.orderBy() == null) ? InterestOrderBy.name : request.orderBy();
    final Order direction = (request.direction() == null) ? Order.ASC : request.direction();
    final String cursor = request.cursor();
    final LocalDateTime after = request.after();
    final int limit = request.limit();

    Slice<Interest> slices = interestRepository.findAll(
        keyword, orderBy, direction, cursor, after, limit);
    log.info("REQ userId={}, keyword={}, orderBy={}, direction={}, cursor={}, after={}, limit={}",
        userId, keyword, orderBy, direction, cursor, after, limit);

    List<Interest> interests = slices.getContent();

    // 관심사 Id 수집
    Set<Long> interestIds = interests.stream().map(Interest::getId).collect(Collectors.toSet());
    // 내가 구독중인 관심사 ID
    Set<Long> subscribedIds = subscribeRepository.findSubscribedByInterestIds(userId,
        interestIds);
    // dto 채우기
    List<InterestDto> interestDtos = new ArrayList<>(interests.size());
    for (Interest interest : interests) {
      List<String> keywords = interest.getKeywords().stream()
          .map(ik -> ik.getKeyword().getKeyword())
          .toList();
      int subscriberCount = interest.getSubscriberCount();
      boolean subscribedByMe = subscribedIds.contains(interest.getId());
      InterestDto dto = interestMapper.toInterestDto(interest, keywords, subscribedByMe,
          subscriberCount);

      log.info("DBG dto id={}, name={}, subscriberCount={} subscribedByMe={}",
          dto.id(), dto.name(), dto.subscriberCount(), dto.subscribedByMe());
      interestDtos.add(dto);
    }

    long totalElements = interestRepository.countFilteredTotalElements(keyword);
    boolean hasNext = slices.hasNext();

    String nextCursor = null;
    LocalDateTime nextAfter = null;
    if (hasNext) {
      Interest last = interests.get(interests.size() - 1);

      if (request.orderBy() == InterestOrderBy.name) {
        nextCursor = last.getName();
      } else if (request.orderBy() == InterestOrderBy.subscriberCount) {
        nextCursor = String.valueOf(last.getId());
      }
      nextAfter = last.getCreatedAt();
    }

    return new CursorPageResponseInterestDto(interestDtos, nextCursor, nextAfter,
        slices.getSize(), totalElements, hasNext);
  }

  @Override
  @Transactional
  public InterestDto updateInterestKeywords(
      InterestUpdateRequest request, Long interestId) {

    Interest interest = interestRepository.findById(interestId)
        .orElseThrow(InterestNotFoundException::new);

    updateKeywords(interest, request.keywords());

    List<String> keywords = interest.getKeywords().stream()
        .map(ik -> ik.getKeyword().getKeyword())
        .collect(Collectors.toList());

    // 키워드 수정 이벤트 발행
    eventPublisher.publishEvent(InterestUpdatedEvent.of(interest.getId(), keywords));

    log.info("interestId = {}, 관심사 키워드 수정 완료 : {}", interestId, keywords);
    return interestMapper.toDto(interest, keywords, false);
  }

  @Override
  @Transactional
  public void deleteInterest(Long interestId) {
    Interest interest = interestRepository.findById(interestId)
        .orElseThrow(InterestNotFoundException::new);

    List<Long> articleIds = interestArticlesRepository.findArticleIdsByInterestId(interestId);
    log.info("관심사({})와 연결된 기사 수: {}", interest.getName(), articleIds.size());

    if (articleIds.isEmpty()) {
      interestRepository.delete(interest);
      return;
    }

    List<Long> usedElsewhere =
        interestArticlesRepository.findArticleIdsUsedByOtherInterests(articleIds, interestId);

    List<Long> toDelete = articleIds.stream()
        .filter(id -> !usedElsewhere.contains(id))
        .toList();

    int deletedCount = toDelete.size();
    int undeletedCount = usedElsewhere.size();

    if (!toDelete.isEmpty()) {
      articleRepository.markAsDeleted(toDelete);
      log.info("논리 삭제된 기사 수: {}", deletedCount);
    }

    log.info("삭제 제외된 기사 수(다른 관심사에서 사용 중): {}", undeletedCount);

    interestRepository.delete(interest);
    eventPublisher.publishEvent(InterestDeletedEvent.of(interest.getId()));

    log.info("관심사 삭제 완료: {}", interest.getName());
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
    if (name1 == null || name2 == null) {
      return 0.0;
    }
    LevenshteinDistance levenshtein = LevenshteinDistance.getDefaultInstance();
    int distance = levenshtein.apply(name1, name2);
    int maxLength = Math.max(name1.length(), name2.length());
    return 1.0 - ((double) distance / maxLength);
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
    if (toRemove.isEmpty()) {
      return;
    }

    List<Keyword> removedKeywords = toRemove.values().stream()
        .map(InterestKeyword::getKeyword)
        .toList();

    interest.getKeywords().removeAll(toRemove.values());

    List<Long> removedKeywordIds = removedKeywords.stream()
        .map(Keyword::getId)
        .toList();

    List<Long> relatedArticleIds =
        interestArticleKeywordRepository.findArticleIdsByKeywordIds(removedKeywordIds);
    log.info("고아 키워드 관련 기사 수: {}", relatedArticleIds.size());

    if (!relatedArticleIds.isEmpty()) {
      List<Long> usedElsewhere = interestArticleKeywordRepository.findArticlesUsedElsewhere(
          relatedArticleIds, removedKeywordIds, interest.getId());

      List<Long> toDelete = relatedArticleIds.stream()
          .filter(id -> !usedElsewhere.contains(id))
          .toList();

      if (!toDelete.isEmpty()) {
        articleRepository.markAsDeleted(toDelete);
        log.info("논리 삭제된 기사 수: {}", toDelete.size());
      }

      log.info("삭제 제외된 기사 수(다른 관심사/키워드 사용 중): {}", usedElsewhere.size());
    }

    List<Keyword> orphanKeywords = keywordRepository.findOrphanKeywordsIn(removedKeywords);
    keywordRepository.deleteAll(orphanKeywords);
    log.info("고아 키워드 삭제 완료: {}", orphanKeywords.size());
  }
}
