package com.monew.monew_api.subscribe.repository;

import com.monew.monew_api.domain.user.User;
import com.monew.monew_api.interest.entity.Interest;
import com.monew.monew_api.subscribe.entity.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {

  boolean existsByInterestAndUser(Interest interest, User user);

  @Query("SELECT s.interest.id FROM Subscribe s " +
      "WHERE s.user.id = :userId AND s.interest.id IN :interestIds")
  Set<Long> findSubscribedByInterestIds(@Param("userId") Long userId,
      @Param("interestIds") Set<Long> interestIds);

  Optional<Subscribe> findByInterestAndUser(Interest interest, User user);

  // 관심사별로 구독자 수 벌크 집계
  @Query("SELECT s.interest.id AS interestId, COUNT(s.id) AS count " +
      "FROM Subscribe s WHERE s.interest.id IN :interestIds GROUP BY s.interest.id")
  List<InterestCountProjection> countByInterestIds(@Param("interestIds") Set<Long> interestIds);

  @Query("""
      SELECT s FROM Subscribe s
      JOIN FETCH s.user
      JOIN FETCH s.interest
      WHERE s.interest.id IN :interestIds
      AND s.user.deletedAt IS NULL
  """)
  List<Subscribe> findAllByInterestIds(Set<Long> interestIds);

  interface InterestCountProjection {

    Long getInterestId();
    Long getCount();
  }
}
