//package com.monew.monew_api.useractivity.tempEntity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.LocalDateTime;
//
//@Entity(name = "ArticleView_temp")
//@Table(name = "article_views")
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AllArgsConstructor
//@Builder
//public class ArticleView {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "article_id", nullable = false)
//    private Long articleId;
//
//    @Column(name = "user_id", nullable = false)
//    private Long userId;
//
//    @Column(name = "created_at", nullable = false)
//    private LocalDateTime createdAt;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "article_id", insertable = false, updatable = false)
//    private Article article;
//}