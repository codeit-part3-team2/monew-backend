package com.monew.monew_api.interest.repository;

import com.monew.monew_api.interest.entity.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InterestRepository extends JpaRepository<Interest, Long>, InterestRepositoryCustom {

  boolean existsByName(String name);

  @Query("SELECT i FROM Interest i JOIN FETCH i.keywords k JOIN FETCH k.keyword")
  List<Interest> findAllWithKeywords();
}
