package com.monew.monew_api.interest.repository;

import com.monew.monew_api.interest.dto.InterestOrderBy;
import com.monew.monew_api.interest.entity.Interest;
import com.querydsl.core.types.Order;
import java.time.LocalDateTime;
import org.springframework.data.domain.Slice;

public interface InterestRepositoryCustom {

  Slice<Interest> findAll(
      String keyword,
      InterestOrderBy sortBy,
      Order direction,
      String cursor,
      LocalDateTime after,
      int limit
  );

  Long countFilteredTotalElements(String keyword);
}

