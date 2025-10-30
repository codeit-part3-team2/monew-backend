package com.monew.monew_api.interest.repository;

import com.monew.monew_api.interest.entity.QInterest;
import com.monew.monew_api.interest.dto.InterestOrderBy;
import com.monew.monew_api.interest.entity.Interest;
import com.monew.monew_api.interest.entity.QInterestKeyword;
import com.monew.monew_api.interest.entity.QKeyword;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class InterestRepositoryCustomImpl implements InterestRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  private static final QInterest i =  QInterest.interest;
  private static final QKeyword k = QKeyword.keyword1;
  private static final QInterestKeyword ik = QInterestKeyword.interestKeyword;

  private Expression<?> sortExpression(InterestOrderBy sortBy) {
    return switch (sortBy) {
      case name -> i.name;
      case subscriberCount -> i.subscriberCount;
    };
  }

  // 키워드 검색으로 얻은 관심사를 통해 그 관심사에 포함된 모든 키워드 반환
  // ID로 필터링 후 fetch join으로 로딩
  @Override
  public Slice<Interest> findAll(String searchKeyword, InterestOrderBy sortBy, Direction direction,
      String cursor, LocalDateTime after, int limit) {
    Expression<?> sortExpr = sortExpression(sortBy);

    // id, 정렬칼럼 받기
    List<Tuple> rows = queryFactory
        .selectDistinct(i.id, sortExpr)
        .from(i)
        .leftJoin(i.keywords, ik)
        .leftJoin(ik.keyword, k)
        .where(
            containsInterestOrKeyword(searchKeyword),
            createdAfter(after),
            createCursorPredicate(sortBy, direction, cursor)
        )
        .orderBy(
            sortBy(sortBy, direction),
            secondSortBy(direction)
        )
        .limit(limit + 1)
        .fetch();

    boolean hasNext = rows.size() > limit;
    if (hasNext) rows = rows.subList(0, limit);

    if (rows.isEmpty()) {
      return new SliceImpl<>(Collections.emptyList(), PageRequest.of(0, limit), false);
    }

    // id만 추출
    List<Long> interestIds = rows.stream()
        .map(t -> t.get(i.id))
        .toList();

    // 관심사 id 포함 전체 로딩
    List<Interest> results = queryFactory
        .selectFrom(i)
        .distinct()
        .leftJoin(i.keywords, ik).fetchJoin()
        .leftJoin(ik.keyword, k).fetchJoin()
        .where(i.id.in(interestIds))
        .orderBy(
            sortBy(sortBy, direction),
            secondSortBy(direction)
        )
        .fetch();

    return new SliceImpl<>(results, PageRequest.of(0, limit), hasNext);
  }

  // 관심사명 OR 키워드명 부분일치
  private BooleanExpression containsInterestOrKeyword(String keyword) {
    if (keyword == null || keyword.isBlank()) return null;
    return i.name.containsIgnoreCase(keyword)
        .or(k.keyword.containsIgnoreCase(keyword));
  }

  // after
  private BooleanExpression createdAfter(LocalDateTime after) {
    if (after == null) return null;
    return i.createdAt.goe(after);
  }

  // 커서 조건: 정렬 기준별 비교
  private BooleanExpression createCursorPredicate(InterestOrderBy sortBy, Direction dir, String cursor) {
    if (cursor == null || cursor.isBlank()) return null;

    return switch (sortBy) {
      case name -> (dir == Direction.ASC) ? i.name.gt(cursor) : i.name.lt(cursor);
      case subscriberCount -> {
        int v = Integer.parseInt(cursor);
        yield (dir == Direction.ASC) ? i.subscriberCount.gt(v) : i.subscriberCount.lt(v);
      }
    };
  }

  // 정렬 지정
  private OrderSpecifier<?> sortBy(InterestOrderBy sortBy, Direction dir) {
    boolean asc = (dir == Direction.ASC);
    return switch (sortBy) {
      case name -> asc ? i.name.asc() : i.name.desc();
      case subscriberCount -> asc ? i.subscriberCount.asc() : i.subscriberCount.desc();
    };
  }

  // 보조정렬: 동일값일 때는 id로 정렬하기!!
  private OrderSpecifier<?> secondSortBy(Direction dir) {
    return (dir == Direction.ASC) ? i.id.asc() : i.id.desc();
  }

  @Override
  public long countFilteredTotalElements(String keyword, InterestOrderBy orderBy,
      Direction direction) {

    JPAQuery<Long> query = queryFactory
        .select(i.countDistinct())
        .from(i);

    // keyword가 있을 때만 조인
    if (keyword != null && !keyword.isBlank()) {
      query
          .leftJoin(i.keywords, ik)
          .leftJoin(ik.keyword, k);
    }

    query.where(containsInterestOrKeyword(keyword));
    return query.fetchOne();
  }
}


