package com.monew.monew_api.interest.repository;

import com.monew.monew_api.interest.entity.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InterestRepository extends JpaRepository<Interest, Long>, InterestRepositoryCustom {

  boolean existsByName(String name);

  /** 모든 관심사와 해당 관심사에 연결된 키워드들을 함께 조회 */
  @Query("""
        SELECT i
        FROM Interest i
        JOIN FETCH i.keywords ik
        JOIN FETCH ik.keyword
    """)
  List<Interest> findAllWithKeywords();
}
