package com.monew.monew_api.domain.interest.repository;

import com.monew.monew_api.domain.interest.dto.InterestSortBy;
import com.monew.monew_api.domain.interest.entity.Interest;
import com.monew.monew_api.domain.interest.entity.QInterest;
import com.monew.monew_api.domain.interest.entity.QInterestKeyword;
import com.monew.monew_api.domain.interest.entity.QKeyword;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
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
  private final QInterest i =  QInterest.interest;
  private final QKeyword k = QKeyword.keyword1;
  private final QInterestKeyword ik = QInterestKeyword.interestKeyword;

 @Override
  public Slice<Interest> findAll(
      String searchKeyword, InterestSortBy sortBy, Direction direction,
      String cursor, LocalDateTime after, int limit) {

    BooleanBuilder builder = new BooleanBuilder();

    // 관심사명과 키워드 부분 일치 필터링
    if (searchKeyword != null && !searchKeyword.isBlank()) {
      builder.and(i.name.containsIgnoreCase(searchKeyword)
          .or(k.keyword.containsIgnoreCase(searchKeyword)));
    }

    if (cursor != null && !cursor.isBlank()) {
      if (sortBy == InterestSortBy.NAME) {
        if (direction == Direction.ASC) {
          builder.and(i.name.gt(cursor));
        } else {
          builder.and(i.name.lt(cursor));
        }
      }
      else if (sortBy == InterestSortBy. SUBSCRIBER_COUNT){
        int value = Integer.parseInt(cursor);
        if (direction == Direction.ASC) {
          builder.and(i.subscriberCount.gt(value));
        } else {
          builder.and(i.subscriberCount.lt(value));
        }
      }
    }

    // 정렬조건 + 기본정렬(createdAt)
    OrderSpecifier<?> order;
    OrderSpecifier<?> createdAtOrder = i.createdAt.asc();

    if (sortBy == InterestSortBy.NAME){
      order = (direction == Direction.ASC)
          ? i.name.asc()
          : i.name.desc();
    } else {
      order = (direction == Direction.ASC)
          ? i.subscriberCount.asc()
          : i.subscriberCount.desc();

      createdAtOrder = (direction == Direction.ASC)
          ? i.createdAt.asc()
          : i.createdAt.desc();
    }

    // 관심사와 키워드 함께 조회 -> N+1 문제 방지
    JPAQuery<Interest> query = queryFactory
        .selectFrom(i)
        .distinct()
        .leftJoin(i.keywords, ik).fetchJoin() // 연관된 키워드 한번에 로딩
        .leftJoin(ik.keyword, k).fetchJoin()
        .where(builder);

    query.orderBy(order, createdAtOrder);

    // limit +1로 hasNext 판단
    List<Interest> results = query.limit(limit + 1).fetch();
    boolean hasNext = results.size() > limit;

    if(hasNext){
      results = results.subList(0, limit);
    }

    //Slice 반환
    Pageable pageable = PageRequest.of(0, limit);
    return new SliceImpl<>(results, pageable, hasNext);
 }


  @Override
  public long countFilteredTotalElements(String keyword, InterestSortBy sortBy,
      Direction direction) {

    BooleanBuilder builder = new BooleanBuilder();

    if(keyword != null && !keyword.isBlank()) {
      builder.and(i.name.containsIgnoreCase(keyword))
          .or(k.keyword.containsIgnoreCase(keyword));
    }

    JPAQuery<Long> query = queryFactory
        .select(i.countDistinct())
        .from(i);

    // 키워드 있을 때만 조인 하도록 설정
    if (keyword != null && !keyword.isBlank()) {
      query.leftJoin(i.keywords, ik)
          .leftJoin(ik.keyword, k);
    }

    query.where(builder);

    Long count = query.fetchOne();
    return (count == null) ? 0L : count;
  }
}
