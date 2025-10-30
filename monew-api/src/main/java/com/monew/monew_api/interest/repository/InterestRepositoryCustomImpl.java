package com.monew.monew_api.interest.repository;

import com.monew.monew_api.interest.entity.QInterest;
import com.monew.monew_api.interest.dto.InterestOrderBy;
import com.monew.monew_api.interest.entity.Interest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class InterestRepositoryCustomImpl implements InterestRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  private static final QInterest i = QInterest.interest;

  @Override
  public Slice<Interest> findAll(
      String searchKeyword, InterestOrderBy sortBy, Direction direction,
      String cursor, LocalDateTime after, int limit
  ) {

    BooleanBuilder builder = new BooleanBuilder();
    addSearchConditions(builder, searchKeyword); // 관심사, 키워드 부분일치 조건

    if (cursor != null && !cursor.isBlank()) {
      builder.and(createCursorCondition(sortBy, direction, cursor)); // 커서 조건
    }

    OrderSpecifier<?> orderSpecifier = createOrderSpecifier(sortBy, direction);
    OrderSpecifier<?> idOrderSpecifier = direction == Direction.ASC ? i.id.asc() : i.id.desc();

    List<Interest> results = queryFactory
        .selectFrom(i)
        .where(builder)
        .orderBy(orderSpecifier, idOrderSpecifier)
        .limit(limit + 1)
        .fetch();

    boolean hasNext = results.size() > limit;
    if (hasNext) {
      results.remove(limit);
    }

    Pageable pageable = PageRequest.of(0, limit);
    return new SliceImpl<>(results, pageable, hasNext);
  }


  private BooleanBuilder createCursorCondition(InterestOrderBy sortBy, Direction direction,
      String cursor) {
    BooleanBuilder builder = new BooleanBuilder();

    if (sortBy == InterestOrderBy.subscriberCount) {
      Long cursorSubscriberCount = Long.parseLong(cursor);
      handleSubscriberCountCursor(builder, cursorSubscriberCount, direction);
    } else {
      handleNameCursor(builder, cursor, direction);
    }
    return builder;
  }


  private void handleSubscriberCountCursor(BooleanBuilder builder, Long cursorSubscriberCount,
      Direction direction) {
    if (direction == Direction.DESC) {
      builder.and(i.subscriberCount.lt(cursorSubscriberCount));
    } else {
      builder.and(i.subscriberCount.gt(cursorSubscriberCount));
    }
  }


  private void handleNameCursor(BooleanBuilder builder, String cursor, Direction direction) {
    if (direction == Direction.DESC) {
      builder.and(i.name.lt(cursor));
    } else {
      builder.and(i.name.eq(cursor));
    }
  }


  private OrderSpecifier<?> createOrderSpecifier(InterestOrderBy sortBy, Direction direction) {
    Order order = (direction == Direction.DESC) ? Order.DESC : Order.ASC;

    switch (sortBy) {
      case subscriberCount:
        return new OrderSpecifier<>(order, i.subscriberCount);
      case name:
        return new OrderSpecifier<>(order, i.name);
      default:
        throw new IllegalStateException("Unhandled sort by: " + sortBy);
    }
  }


  private void addSearchConditions(BooleanBuilder builder, String searchKeyword) {
    if (searchKeyword != null && !searchKeyword.isEmpty()) {
      builder.and(
          i.name.containsIgnoreCase(searchKeyword)
              .or(i.keywords.any().keyword.keyword.containsIgnoreCase(searchKeyword))
      );
    }
  }


  @Override
  public long countFilteredTotalElements(String keyword, InterestOrderBy sortBy,
      Direction direction) {
    QInterest i = QInterest.interest;

    BooleanBuilder builder = new BooleanBuilder();
    addSearchConditions(builder, keyword);

    Long count = queryFactory
        .select(i.count())
        .from(i)
        .where(builder)
        .fetchOne();

    return count != null ? count : 0;
  }
}

