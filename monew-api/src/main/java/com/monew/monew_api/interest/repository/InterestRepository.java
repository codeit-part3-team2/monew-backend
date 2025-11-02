package com.monew.monew_api.interest.repository;

import com.monew.monew_api.interest.entity.Interest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

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

  /** 특정 관심사와 해당 관심사에 연결된 키워드들을 함께 조회
   * (이벤트에서 필요해서 추가했어요)
   */
  @EntityGraph(attributePaths = {"keywords", "keywords.keyword"})
  Optional<Interest> findById(Long id);
}
