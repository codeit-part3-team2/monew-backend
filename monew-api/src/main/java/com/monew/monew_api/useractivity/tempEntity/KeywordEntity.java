package com.monew.monew_api.useractivity.tempEntity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "keywords")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class KeywordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String keyword;

    @Column(name = "created_at", nullable = false)  // ← 추가!
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)  // ← 추가!
    private LocalDateTime updatedAt;
}