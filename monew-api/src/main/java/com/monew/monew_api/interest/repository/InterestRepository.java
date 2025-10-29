package com.monew.monew_api.interest.repository;

import com.monew.monew_api.interest.entity.Interest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestRepository extends JpaRepository<Interest, Long>,
    InterestRepositoryCustom {

  boolean existsByName(String name);
}
