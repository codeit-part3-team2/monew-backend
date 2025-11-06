package com.monew.monew_api.interest.entity;

import com.monew.monew_api.common.entity.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "interest_keywords")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InterestKeyword extends BaseTimeEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "interest_id", nullable = false)
  private Interest interest;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "keyword_id", nullable = false)
  private Keyword keyword;

  public static InterestKeyword create(Interest interest, Keyword keyword) {
    return new InterestKeyword(interest, keyword);
  }

}



