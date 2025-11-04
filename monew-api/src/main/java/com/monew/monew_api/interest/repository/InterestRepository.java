package com.monew.monew_api.interest.repository;

import com.monew.monew_api.interest.entity.Interest;
import com.monew.monew_api.interest.entity.Keyword;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InterestRepository extends JpaRepository<Interest, Long>, InterestRepositoryCustom {

    @Query("""
                SELECT DISTINCT i
                FROM Interest i
                JOIN FETCH i.keywords ik
                JOIN FETCH ik.keyword k
                WHERE k = :keyword
            """)
    List<Interest> findAllByKeyword(@Param("keyword") Keyword keyword);

    /**
     * 특정 관심사와 해당 관심사에 연결된 키워드들을 함께 조회
     * author : 정영진
     * 캐시 데이터 업데이트에 필요
     */
    @EntityGraph(attributePaths = {"keywords", "keywords.keyword"})
    Optional<Interest> findById(Long id);
}
