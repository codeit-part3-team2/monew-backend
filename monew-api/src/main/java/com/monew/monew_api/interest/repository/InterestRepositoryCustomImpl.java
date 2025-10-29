package com.monew.monew_api.interest.repository;

import com.monew.monew_api.interest.entity.QInterest;
import com.monew.monew_api.interest.entity.QInterestKeyword;
import com.monew.monew_api.interest.entity.QKeyword;
import com.monew.monew_api.interest.dto.InterestOrderBy;
import com.monew.monew_api.interest.entity.Interest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
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
  private final QInterest i = QInterest.interest;

  @Override
  public Slice<Interest> findAll(
      String searchKeyword, InterestOrderBy sortBy, Direction direction,
      String cursor, LocalDateTime after, int limit) {

    BooleanBuilder builder = new BooleanBuilder();

    if (after != null){
      builder.and(i.updatedAt.goe(after));
    }

    // 커서 조건
    if (cursor != null && !cursor.isBlank()) {
      if (sortBy == InterestOrderBy.name) {
        if (direction == Direction.ASC) builder.and(i.name.gt(cursor));
        else builder.and(i.name.lt(cursor));
      } else if (sortBy == InterestOrderBy.subscriberCount) {
        int v = Integer.parseInt(cursor);
        if (direction == Direction.ASC) builder.and(i.subscriberCount.gt(v));
        else builder.and(i.subscriberCount.lt(v));
      }
    }

    // 정렬 조건
    OrderSpecifier<?> primaryOrder =
        (sortBy == InterestOrderBy.name)
            ? (direction == Direction.ASC ? i.name.asc() : i.name.desc())
            : (direction == Direction.ASC ? i.subscriberCount.asc() : i.subscriberCount.desc());

    OrderSpecifier<?> createdAtOrder =
        (direction == Direction.ASC ? i.createdAt.asc() : i.createdAt.desc());

    QInterestKeyword ikFilter = new QInterestKeyword("ikFilter"); // 필터링 판별 전용
    QKeyword kFilter = new QKeyword("kFilter");

    QInterestKeyword ikAll = new QInterestKeyword("ikAll"); // 전체 로딩 전용
    QKeyword kAll = new QKeyword("kAll");

    JPAQuery<Interest> query = queryFactory
        .selectFrom(i)
        .distinct();


    // 검색어가 있는 경우
    if (searchKeyword != null && !searchKeyword.isBlank()) {
      BooleanExpression nameLike = i.name.containsIgnoreCase(searchKeyword);

      // 같은 관심사에 포함된 키워드 중 검색어가 포함되는 행만 매칭!
      query.leftJoin(i.keywords, ikFilter)
          .on(ikFilter.interest.eq(i))
          .leftJoin(ikFilter.keyword, kFilter)
          .on(kFilter.keyword.containsIgnoreCase(searchKeyword));

      builder.and(nameLike.or(kFilter.id.isNotNull()));
    }

    // 전체 키워드 로딩
    query.leftJoin(i.keywords, ikAll).fetchJoin()
        .leftJoin(ikAll.keyword, kAll).fetchJoin();

    query.where(builder)
        .orderBy(primaryOrder, createdAtOrder);

    List<Interest> results = query.limit(limit + 1).fetch();

    boolean hasNext = results.size() > limit;
    if (hasNext) results = results.subList(0, limit);

    Pageable pageable = PageRequest.of(0, limit);
    return new SliceImpl<>(results, pageable, hasNext);
  }

  @Override
  public long countFilteredTotalElements(String keyword, InterestOrderBy sortBy, Direction direction) {

    BooleanBuilder where = new BooleanBuilder();

    QInterestKeyword ikFilter = new QInterestKeyword("ikFilter");
    QKeyword kFilter = new QKeyword("kFilter");

    JPAQuery<Long> query = queryFactory
        .select(i.id.countDistinct())
        .from(i);

    if (keyword != null && !keyword.isBlank()) {
      BooleanExpression nameLike = i.name.containsIgnoreCase(keyword);

      query.leftJoin(i.keywords, ikFilter)
          .on(ikFilter.interest.eq(i))
          .leftJoin(ikFilter.keyword, kFilter)
          .on(kFilter.keyword.containsIgnoreCase(keyword));

      where.and(nameLike.or(kFilter.id.isNotNull()));
    }

    Long count = query.where(where).fetchOne();
    return (count == null) ? 0L : count;
  }
}

