package com.monew.monew_api.interest.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.monew.monew_api.article.repository.ArticleRepository;
import com.monew.monew_api.article.repository.InterestArticleKeywordRepository;
import com.monew.monew_api.article.repository.InterestArticlesRepository;
import com.monew.monew_api.common.exception.interest.InterestDuplicatedException;
import com.monew.monew_api.interest.dto.request.InterestUpdateRequest;
import com.monew.monew_api.interest.event.InterestDeletedEvent;
import com.monew.monew_api.interest.event.InterestUpdatedEvent;
import com.monew.monew_api.user.User;
import com.monew.monew_api.interest.TestInterestForm;
import com.monew.monew_api.interest.dto.InterestOrderBy;
import com.monew.monew_api.interest.dto.request.CursorPageRequestInterestDto;
import com.monew.monew_api.interest.dto.request.InterestRegisterRequest;
import com.monew.monew_api.interest.dto.response.CursorPageResponseInterestDto;
import com.monew.monew_api.interest.dto.response.InterestDto;
import com.monew.monew_api.interest.entity.Interest;
import com.monew.monew_api.interest.entity.Keyword;
import com.monew.monew_api.interest.mapper.InterestMapper;
import com.monew.monew_api.interest.repository.InterestRepository;
import com.monew.monew_api.interest.repository.KeywordRepository;
import com.monew.monew_api.subscribe.repository.SubscribeRepository;
import com.querydsl.core.types.Order;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort.Direction;

@ExtendWith(MockitoExtension.class)
public class InterestServiceTest {

  @Mock
  InterestRepository interestRepository;

  @Mock
  KeywordRepository keywordRepository;

  @Mock
  SubscribeRepository subscribeRepository;

  @Mock
  ArticleRepository articleRepository;

  @Mock
  InterestArticlesRepository interestArticlesRepository;

  @Mock
  InterestArticleKeywordRepository interestArticleKeywordRepository;

  @Mock
  InterestMapper interestMapper;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @InjectMocks
  InterestServiceImpl interestService;

  @DisplayName("관심사 생성 실패 - 유사도 0.8이상이면 중복 예외")
  @Test
  void createInterest_fail() {
    String newName = "interest1";
    List<String> keywords = List.of("keyword1", "keyword2");
    InterestRegisterRequest request = new InterestRegisterRequest(newName, keywords);

    Interest existing = TestInterestForm.create("interest2", List.of());
    when(interestRepository.findAll()).thenReturn(List.of(existing));

    assertThatThrownBy(() -> interestService.createInterest(request))
        .isInstanceOf(InterestDuplicatedException.class);

    verify(interestRepository, never()).save(any(Interest.class));
    verify(keywordRepository, never()).save(any(Keyword.class));

  }

  @DisplayName("관심사 생성 성공 - 유사도 중복 없음")
  @Test
  void createInterest_success() {
    String interestName = "interest1";
    List<String> keywords = List.of("keyword1", "keyword2");
    InterestRegisterRequest request = new InterestRegisterRequest(interestName, keywords);
    Interest snapshot = TestInterestForm.create(interestName, keywords);

    // 유사도 검사 통과
    when(interestRepository.findAll()).thenReturn(Collections.emptyList());
    // 키워드 조회 및 저장
    when(keywordRepository.findByKeyword("keyword1")).thenReturn(Optional.empty());
    when(keywordRepository.findByKeyword("keyword2")).thenReturn(Optional.empty());
    when(keywordRepository.save(any(Keyword.class)))
        .thenAnswer(invocationOnMock -> {
          return invocationOnMock.getArgument(0);
        });
    when(interestRepository.save(any(Interest.class)))
        .thenReturn(snapshot);

    InterestDto expected = new InterestDto(
        null,
        interestName,
        List.of("keyword1", "keyword2"),
        0L,
        false);

    when(interestMapper.toDto(eq(snapshot), anyList(), eq(false))).thenReturn(expected);

    InterestDto result = interestService.createInterest(request);

    assertThat(result.name()).isEqualTo(interestName);
    assertThat(result.keywords()).contains("keyword1", "keyword2");
    assertThat(result.subscriberCount()).isEqualTo(0);

    verify(interestRepository).save(any(Interest.class));
  }

  @DisplayName("관심사 목록 조회 - name DESC")
  @Test
  void getInterests() {
    User user = new User("user@test.com", "user", "password");
    Long userId = user.getId();

    CursorPageRequestInterestDto request = new CursorPageRequestInterestDto(
        null,
        InterestOrderBy.name, Order.DESC, null, null, 3);

    Interest interest = TestInterestForm.create("interest1", List.of("k1", "k2"));
    Slice<Interest> slices = new SliceImpl<>(List.of(interest),
        PageRequest.of(0, 3), false);

    when(interestRepository.findAll(request.keyword(),
        request.orderBy(), request.direction(),
        request.cursor(), request.after(),
        request.limit())).thenReturn(slices);

    when(interestRepository.countFilteredTotalElements(any())).thenReturn(1L);

    CursorPageResponseInterestDto result = interestService.getInterests(userId, request);

    assertThat(result.content()).hasSize(1);
    assertThat(result.totalElements()).isEqualTo(1L);
    assertThat(result.hasNext()).isEqualTo(false);
    verify(interestRepository).findAll(request.keyword(),
        request.orderBy(), request.direction(),
        request.cursor(), request.after(),
        request.limit());
  }

  @DisplayName("관심사 수정 시 키워드 추가/삭제 - 관련 기사 없음")
  @Test
  void updateInterestKeywords() {
    // keyword1 삭제하고 keyword2 추가
    String name = "interest1";
    Interest interest = TestInterestForm.create(name, List.of("keyword1"));
    InterestUpdateRequest request = new InterestUpdateRequest(List.of("keyword2"));

    when(interestRepository.findById(any(Long.class)))
        .thenReturn(Optional.of(interest));

    when(keywordRepository.findAllByKeywordIn(argThat(list ->
        list.size() == 1 && list.contains("keyword2")
    ))).thenReturn(List.of());

    when(keywordRepository.save(any(Keyword.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    when(interestArticleKeywordRepository.findArticleIdsByKeywordIds(anyList()))
        .thenReturn(Collections.emptyList());

    // 고아 키워드 삭제: keyword1
    when(keywordRepository.findOrphanKeywordsIn(anyList()))
        .thenReturn(List.of(new Keyword("keyword1")));

    when(interestMapper.toDto(eq(interest), anyList(), eq(false)))
        .thenAnswer(inv -> {
          Interest it = inv.getArgument(0);
          @SuppressWarnings("unchecked")
          List<String> kws = inv.getArgument(1);
          return new InterestDto(it.getId(), it.getName(), kws, (long) it.getSubscriberCount(), false);
        });

    InterestDto result = interestService.updateInterestKeywords(request, interest.getId());

    assertThat(result.keywords()).containsExactly("keyword2");
    verify(keywordRepository).save(any(Keyword.class));
    verify(keywordRepository).deleteAll(any());
    verify(interestArticleKeywordRepository).findArticleIdsByKeywordIds(anyList());
    verify(interestArticleKeywordRepository, never()).findArticlesUsedElsewhere(anyList(), anyList(), anyLong());
    verify(articleRepository, never()).markAsDeleted(anyList());

    // 이벤트 검증
    ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
    verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());

    Object published = eventCaptor.getValue();
    assertThat(published).isInstanceOf(InterestUpdatedEvent.class);

    InterestUpdatedEvent ev = (InterestUpdatedEvent) published;
    assertThat(ev.interestId()).isEqualTo(interest.getId());
    assertThat(ev.newKeywords()).containsExactly("keyword2");
  }

  @DisplayName("관심사 삭제- 관련 기사 없으면 바로 삭제")
  @Test
  void deleteInterest_noArticles() {
    Interest interest = TestInterestForm.create("interest1", List.of("k1", "k2"));
    Long interestId = interest.getId();

    when(interestRepository.findById(any(Long.class))).thenReturn(Optional.of(interest));
    when(interestArticlesRepository.findArticleIdsByInterestId(interestId))
        .thenReturn(List.of());

    interestService.deleteInterest(interestId);

    verify(interestRepository).delete(interest);
  }

  @DisplayName("관심사 삭제 - 일부 기사만 다른 관심사에서 사용 중이면 나머지만 논리 삭제 후 바로 삭제")
  @Test
  void deleteInterest_someUsedElsewhere() {
    Interest interest = TestInterestForm.create("interest1", List.of("k1", "k2"));
    Long interestId = interest.getId();

    when(interestRepository.findById(interestId)).thenReturn(Optional.of(interest));

    // 연결된 기사[1,2,3] 중 [2]는 다른 관심사에서도 사용됨
    List<Long> articleIds = List.of(1L, 2L, 3L);
    when(interestArticlesRepository.findArticleIdsByInterestId(interestId))
        .thenReturn(articleIds);
    when(interestArticlesRepository.findArticleIdsUsedByOtherInterests(articleIds, interestId))
        .thenReturn(List.of(2L));

    interestService.deleteInterest(interestId);

    // 논리 삭제 대상: [1,3]
    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<Long>> captor = ArgumentCaptor.forClass(List.class);
    verify(articleRepository).markAsDeleted(captor.capture());
    assertThat(captor.getValue()).containsExactlyInAnyOrder(1L, 3L);

    verify(interestRepository).delete(interest);

    // 이벤트 검증
    ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
    verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());
    Object published = eventCaptor.getValue();
    assertThat(published).isInstanceOf(InterestDeletedEvent.class);
    InterestDeletedEvent ev = (InterestDeletedEvent) published;
    assertThat(ev.interestId()).isEqualTo(interestId);

    // then 4) 연관 리포지토리 호출 인자 검증(의도 확인)
    verify(interestArticlesRepository).findArticleIdsByInterestId(interestId);
    verify(interestArticlesRepository)
        .findArticleIdsUsedByOtherInterests(articleIds, interestId);

    // then 5) 불필요한 호출이 없는지(선택적 강화)
    verify(interestRepository, never()).deleteById(anyLong());
    verifyNoMoreInteractions(articleRepository, eventPublisher);
  }
}
