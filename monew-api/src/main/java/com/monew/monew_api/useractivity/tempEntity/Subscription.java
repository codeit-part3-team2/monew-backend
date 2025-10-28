package com.monew.monew_api.useractivity.tempEntity;

import jakarta.persistence.*;
import lombok.*;
import com.monew.monew_api.article.entity.Interest;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscribes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "interest_id", nullable = false)
    private Long interestId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interest_id", insertable = false, updatable = false)
    private Interest interest;
}