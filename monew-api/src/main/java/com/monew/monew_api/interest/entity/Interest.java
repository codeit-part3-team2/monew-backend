package com.monew.monew_api.interest.entity;

import com.monew.monew_api.common.entity.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "interests")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Interest extends BaseTimeEntity {

  @Column(length = 100, nullable = false, unique = true)
  private String name;

  @Column(nullable = false)
  private int subscriberCount = 0;

  @OneToMany(mappedBy = "interest", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("createdAt ASC")
  private Set<InterestKeyword> keywords = new HashSet<>();

  private Interest(String name, int subscriberCount) {
    this.name = name;
    this.subscriberCount = subscriberCount;
  }

  public static Interest create(String name) {
    return new Interest(name, 0);
  }

  public InterestKeyword addKeyword(Keyword keyword) {
    InterestKeyword interestKeyword = InterestKeyword.create(this, keyword);
    this.keywords.add(interestKeyword);
    return interestKeyword;
  }

  public void addSubscriberCount(){
    this.subscriberCount++;

  }

  public void cancelSubscriberCount(){
    if(this.subscriberCount > 0){}
    this.subscriberCount--;
  }
}
