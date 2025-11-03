package com.monew.monew_api.interest.repository;

import com.monew.monew_api.interest.entity.Interest;
import com.monew.monew_api.interest.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InterestRepository extends JpaRepository<Interest, Long>, InterestRepositoryCustom {

  @Query("""
        SELECT DISTINCT i
        FROM Interest i
        JOIN FETCH i.keywords ik
        JOIN FETCH ik.keyword k
        WHERE k = :keyword
    """)
  List<Interest> findAllByKeyword(@Param("keyword") Keyword keyword);
}
