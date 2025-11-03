package com.monew.monew_api.subscribe.entity;

import com.monew.monew_api.common.entity.BaseCreatedEntity;
import com.monew.monew_api.user.User;
import com.monew.monew_api.interest.entity.Interest;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subscribes")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Subscribe extends BaseCreatedEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "interest_id",  nullable = false)
  private Interest interest;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  public static Subscribe create(Interest interest, User user) {
    return new Subscribe(interest, user);
  }
}

