package com.monew.monew_api.interest.repository;


import com.monew.monew_api.interest.entity.Keyword;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

  Optional<Keyword> findByKeyword(String keyword);

  List<Keyword> findAllByKeywordIn(Collection<String> keywords);

  @Query("SELECT k FROM Keyword k "
      + "WHERE k IN :keywords AND NOT EXISTS ("
      + "SELECT 1 FROM InterestKeyword ik WHERE ik.keyword = k"
      + ")"
  )
  List<Keyword> findOrphanKeywordsIn(@Param("keywords") Collection<Keyword> keywords);
}
