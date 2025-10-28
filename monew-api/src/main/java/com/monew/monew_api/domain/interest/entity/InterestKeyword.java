package com.monew.monew_api.domain.interest.entity;

import com.monew.monew_api.common.entity.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "interest_keywords")
@Getter @Setter @ToString @Builder
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



