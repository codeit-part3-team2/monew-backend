package com.monew.monew_api.interest.repository;

import com.monew.monew_api.interest.entity.Interest;
import com.monew.monew_api.interest.dto.InterestOrderBy;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import static com.monew.monew_api.interest.entity.QInterestKeyword.interestKeyword;
import static com.monew.monew_api.interest.entity.QKeyword.keyword1;
import static com.monew.monew_api.interest.entity.QInterest.interest;

@Repository
@RequiredArgsConstructor
public class InterestRepositoryCustomImpl implements InterestRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public Slice<Interest> findAll(
      String searchKeyword,
      InterestOrderBy orderBy,
      Order direction,
      String cursor,
      LocalDateTime after,
      int limit
  ) {
    BooleanBuilder builder = new BooleanBuilder();

    if (searchKeyword != null && !searchKeyword.isBlank()) {
      builder.and(
          interest.name.containsIgnoreCase(searchKeyword)
              .or(keyword1.keyword.containsIgnoreCase(searchKeyword))
      );
    }

    if (after != null) {
      builder.and(interest.createdAt.gt(after));
    }

    Order ord = (direction == null || direction == Order.DESC) ? Order.DESC : Order.ASC;
    boolean desc = (ord == Order.DESC);

    Long cursorId = null;
    String cursorName = null;
    Integer cursorCnt = null;

    OrderSpecifier<?>[] orderSpec = new OrderSpecifier<?>[]{};;

    switch (orderBy) {
      case name -> {
        cursorName = (cursor == null || cursor.isBlank()) ? null : cursor;

        orderSpec = new OrderSpecifier<?>[]{
            new OrderSpecifier<>(ord, interest.name)
        };

        if (cursorName != null) {
          builder.and(desc ? interest.name.lt(cursorName) : interest.name.gt(cursorName));
        }
      }

      case subscriberCount -> {
        cursorId = (cursor == null || cursor.isBlank()) ? null : Long.valueOf(cursor);

        if (cursorId != null) {
          Interest base = queryFactory.selectFrom(interest)
              .where(interest.id.eq(cursorId))
              .fetchOne();
          if (base != null)
            cursorCnt = base.getSubscriberCount();
        }

        orderSpec = new OrderSpecifier<?>[]{
            new OrderSpecifier<>(ord, interest.subscriberCount),
            new OrderSpecifier<>(ord, interest.id)
        };

        if (cursorId != null && cursorCnt != null) {
          BooleanExpression cut = desc
              ? interest.subscriberCount.lt(cursorCnt)
              .or(interest.subscriberCount.eq(cursorCnt).and(interest.id.lt(cursorId)))
              : interest.subscriberCount.gt(cursorCnt)
                  .or(interest.subscriberCount.eq(cursorCnt).and(interest.id.gt(cursorId)));
          builder.and(cut);
        }
      }
    }

    List<Interest> rowsPlusOne = queryFactory
        .select(interest)
        .from(interest)
        .leftJoin(interest.keywords, interestKeyword)
        .leftJoin(interestKeyword.keyword, keyword1)
        .where(builder)
        .groupBy(interest.id)
        .orderBy(orderSpec)
        .limit(limit + 1)
        .fetch();

    boolean hasNext = rowsPlusOne.size() > limit;
    List<Interest> content = hasNext ? rowsPlusOne.subList(0, limit) : rowsPlusOne;

    return new SliceImpl<>(content, PageRequest.of(0, limit), hasNext);
  }

  @Override
  public Long countFilteredTotalElements(String keyword) {
    JPAQuery<Long> query = queryFactory
        .select(interest.countDistinct())
        .from(interest);

    BooleanBuilder builder = new BooleanBuilder();

    // keyword가 있을 때만 조인
    if (keyword != null && !keyword.isBlank()) {
      query
          .leftJoin(interest.keywords, interestKeyword)
          .leftJoin(interestKeyword.keyword, keyword1);
      builder.and(
          interest.name.containsIgnoreCase(keyword)
              .or(keyword1.keyword.containsIgnoreCase(keyword))
      );
    }
    Long count = query.where(builder).fetchOne();
    return (count != null) ? count : 0L;
  }
}


