//package com.monew.monew_api.useractivity.tempEntity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity(name = "Interest_temp")
//@Table(name = "interests")
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AllArgsConstructor
//@Builder
//public class Interest {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, unique = true, length = 100)
//    private String name;
//
//    @Column(name = "subscriber_count", nullable = false)
//    private Integer subscriberCount;
//
//    @Column(name = "created_at", nullable = false)
//    private LocalDateTime createdAt;
//
//    @Column(name = "updated_at", nullable = false)
//    private LocalDateTime updatedAt;
//
//    @OneToMany(mappedBy = "interest", fetch = FetchType.LAZY)
//    @Builder.Default
//    private List<InterestsKeywords> interestsKeywords = new ArrayList<>();
//}