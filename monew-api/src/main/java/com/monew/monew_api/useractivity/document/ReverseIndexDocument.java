package com.monew.monew_api.useractivity.document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/*
    역인덱스 문서
    - key : id 패턴 "도메인_{id}_행동"
    - 댓글 작성자: "comment_{id}_author" -> {userIds}
    - 댓글 좋아요: "comment_{id}_likes" -> {userIds}
    - 기사 조회: "article_{id}_views" -> {userIds}
 */
@Document(collection = "reverse_indexes")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReverseIndexDocument {

    @Id
    private String id;

    @Builder.Default
    private Set<String> userIds = new HashSet<>();

    private LocalDateTime createdAt;

    @Indexed(name = "index_ttl", expireAfter = "1h")
    private LocalDateTime updatedAt;

    /**
     * 댓글 작성자 역인덱스 키 생성
     *
     * @param commentId 댓글 ID
     * @return "comment_{commentId}_author"
     */
    public static String makeCommentAuthorKey(Long commentId) {
        return "comment_" + commentId + "_author";
    }

    /**
     * 댓글 좋아요 역인덱스 키 생성
     *
     * @param commentId 댓글 ID
     * @return "comment_{commentId}_likes"
     */
    public static String makeCommentLikesKey(Long commentId) {
        return "comment_" + commentId + "_likes";
    }

    /**
     * 기사 조회 역인덱스 키 생성
     *
     * @param articleId 기사 ID
     * @return "article_{articleId}_views"
     */
    public static String makeArticleViewsKey(Long articleId) {
        return "article_" + articleId + "_views";
    }

    /**
     * Interest 구독자 역인덱스 키 생성
     * @param interestId 관심사 ID
     * @return "interest_{interestId}_subs"
     */
    public static String makeInterestSubscribersKey(Long interestId) {
        return "interest_" + interestId + "_subs";
    }
}