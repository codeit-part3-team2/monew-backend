package com.monew.monew_api.interest.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.monew.monew_api.common.config.QuerydslConfig;
import com.monew.monew_api.interest.entity.Interest;
import com.monew.monew_api.interest.entity.Keyword;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslConfig.class)
public class KeywordRepositoryTest {

  @Autowired
  KeywordRepository keywordRepository;

  @Autowired
  InterestRepository interestRepository;

  @Autowired
  EntityManager em;

  @DisplayName("관심사 안에 포함되지 않는 키워드 조회")
  @Test
  public void findOrphanKeywordsIn() {
    Keyword keyword1 = keywordRepository.save(new Keyword("keyword1"));
    Keyword keyword2 = keywordRepository.save(new Keyword("keyword2"));
    Keyword keyword3 = keywordRepository.save(new Keyword("keyword3")); // 고아 키워드

    Interest interest = Interest.create("interest1");
    interest.addKeyword(keyword1);
    interest.addKeyword(keyword2);
    interestRepository.saveAndFlush(interest);

    em.flush();
    em.clear();

    List<Keyword> orphanKeywords = keywordRepository.findOrphanKeywordsIn(
        List.of(keyword1, keyword2, keyword3));

    assertThat(orphanKeywords).hasSize(1);
    assertThat(orphanKeywords.get(0).getKeyword()).isEqualTo("keyword3");
  }
}
