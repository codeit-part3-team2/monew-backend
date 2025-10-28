package com.monew.monew_api.domain.interest.repository;

import com.monew.monew_api.domain.interest.dto.InterestSortBy;
import com.monew.monew_api.domain.interest.entity.Interest;
import java.time.LocalDateTime;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Direction;

public interface InterestRepositoryCustom {

  Slice<Interest> findAll(
      String keyword,
      InterestSortBy sortBy,
      Direction direction,
      String cursor,
      LocalDateTime after,
      int limit
  );

  long countFilteredTotalElements(
      String keyword,
      InterestSortBy sortBy,
      Direction direction
  );
}

