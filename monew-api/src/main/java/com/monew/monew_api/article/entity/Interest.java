package com.monew.monew_api.article.entity;

import com.monew.monew_api.common.entity.BaseIdEntity;
import com.monew.monew_api.useractivity.tempEntity.InterestsKeywords;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 관심사 테이블
 */
@Getter
@NoArgsConstructor
@Entity
@Table(name = "interests")
public class Interest extends BaseIdEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @OneToMany(mappedBy = "interest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterestArticles> interestArticles = new ArrayList<>();

    @OneToMany(mappedBy = "interest", fetch = FetchType.LAZY)
    private List<InterestsKeywords> interestsKeywords = new ArrayList<>();

    @Column(name = "subscriber_count", nullable = false)
    private Integer subscriberCount;
}
