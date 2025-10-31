package com.monew.monew_api.interest.repository;

import com.monew.monew_api.interest.dto.InterestOrderBy;
import com.monew.monew_api.interest.entity.Interest;
import java.time.LocalDateTime;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Direction;

public interface InterestRepositoryCustom {

  Slice<Interest> findAll(
      String keyword,
      InterestOrderBy sortBy,
      Direction direction,
      String cursor,
      LocalDateTime after,
      int limit
  );

  long countFilteredTotalElements(
      String keyword,
      InterestOrderBy sortBy,
      Direction direction
  );
}

