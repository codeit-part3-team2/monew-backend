//package com.monew.monew_api.useractivity.tempEntity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.LocalDateTime;
//
//@Entity(name = "Article_temp")
//@Table(name = "articles")
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AllArgsConstructor
//@Builder
//public class Article {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, length = 200)
//    private String title;
//
//    @Column(nullable = false, length = 20)
//    private String source;
//
//    @Column(name = "source_url", nullable = false, length = 500, unique = true)
//    private String sourceUrl;
//
//    @Column(nullable = false, length = 200)
//    private String summary;
//
//    @Column(name = "publish_date", nullable = false)
//    private LocalDateTime publishDate;
//
//    @Column(name = "comment_count", nullable = false)
//    private Integer commentCount;
//
//    @Column(name = "view_count", nullable = false)
//    private Integer viewCount;
//
//    @Column(name = "is_deleted", nullable = false)
//    private Boolean isDeleted;
//
//}