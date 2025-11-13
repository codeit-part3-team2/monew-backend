package com.monew.monew_api.interest.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.monew.monew_api.common.config.QuerydslConfig;
import com.monew.monew_api.interest.dto.InterestOrderBy;
import com.monew.monew_api.interest.entity.Interest;
import com.monew.monew_api.interest.entity.Keyword;
import com.querydsl.core.types.Order;
import java.time.LocalDateTime;
import java.util.Comparator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;


@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslConfig.class)
public class InterestRepositoryCustomTest {

  @Qualifier("interestRepositoryCustomImpl")
  @Autowired
  InterestRepositoryCustom interestRepositoryCustom;

  @Autowired
  TestEntityManager em;

  @BeforeEach
  void setUp() {
    Interest i1 = Interest.create("interest1");
    Interest i2 = Interest.create("interest2");
    Interest i3 = Interest.create("interest3");

    // i1: 3명  i2: 2명  i3: 1명 구독
    i1.addSubscriberCount();
    i1.addSubscriberCount();
    i1.addSubscriberCount();
    i2.addSubscriberCount();
    i2.addSubscriberCount();
    i3.addSubscriberCount();

    em.persist(i1);
    em.persist(i2);
    em.persist(i3);

    Keyword k1 = new Keyword("keyword1");
    Keyword k2 = new Keyword("keyword2");
    Keyword k3 = new Keyword("keyword3");
    Keyword k4 = new Keyword("keyword4");

    // i1: k1,k2   i2: k2,k3  i3: k4
    i1.addKeyword(k1);
    i1.addKeyword(k2);
    i2.addKeyword(k2);
    i2.addKeyword(k3);
    i3.addKeyword(k4);

    em.persist(k1);
    em.persist(k2);
    em.persist(k3);
    em.persist(k4);

    em.flush();
    em.clear();
  }

  @Test
  @DisplayName("관심사 전체 조회 - name ASC")
  void testFindAllNameASC() {
    String keyword = null;
    InterestOrderBy orderBy = InterestOrderBy.name;
    Order direction = Order.ASC;
    String cursor = null;
    LocalDateTime after = null;
    int limit = 2;

    Slice<Interest> result = interestRepositoryCustom.findAll(
        keyword, orderBy, direction, cursor, after, limit
    );

    assertThat(result).hasSize(2);
    assertThat(result.hasNext()).isTrue();
    assertThat(result.getContent())
        .isSortedAccordingTo(Comparator.comparing(Interest::getName));

  }

  @Test
  @DisplayName("검색어로 관심사 조회 - subscriberCount DESC")
  void testFindAllSubscriberCountDESC() {
    String keyword = "interest1";
    InterestOrderBy orderBy = InterestOrderBy.subscriberCount;
    Order direction = Order.DESC;
    int limit = 6;

    Slice<Interest> result = interestRepositoryCustom.findAll(
        keyword, orderBy, direction, null, null, limit
    );

    assertThat(result).isNotEmpty();
    assertThat(result.getContent())
        .allMatch(i -> i.getName().contains("interest1"));
  }

  @Test
  @DisplayName("커서 조회 확인 - subscriberCount ASC")
  void testFindAllSubscriberCountASCWithCursor() {
    Slice<Interest> firstSlice = interestRepositoryCustom.findAll(
        null, InterestOrderBy.subscriberCount, Order.ASC, null, null, 2
    );
    assertThat(firstSlice).hasSize(2);
    assertThat(firstSlice.hasNext()).isTrue();

    Interest last = firstSlice.getContent().get(1);
    String nextCursor = String.valueOf(last.getSubscriberCount());
    LocalDateTime after = last.getCreatedAt();

    Slice<Interest> secondSlice = interestRepositoryCustom.findAll(
        null, InterestOrderBy.subscriberCount, Order.DESC, nextCursor, after, 2
    );
    assertThat(secondSlice).isNotEmpty();
  }

  @Test
  @DisplayName("관심사 전체 카운트")
  void testFindAllSubscriberCount() {
    long count = interestRepositoryCustom.countFilteredTotalElements(null);
    assertThat(count).isEqualTo(3);
  }

  @Test
  @DisplayName("검색어로 관심사 카운트")
  void testCountFilteredTotalElementsWithKeyword() {
    long count = interestRepositoryCustom.countFilteredTotalElements("keyword1");
    assertThat(count).isEqualTo(1);
  }

}
