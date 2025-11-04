package com.monew.monew_api.useractivity.repository.Impl;

import com.monew.monew_api.useractivity.document.UserActivityCacheDocument;
import com.monew.monew_api.useractivity.dto.ArticleViewActivityDto;
import com.monew.monew_api.useractivity.dto.CommentActivityDto;
import com.monew.monew_api.useractivity.dto.CommentLikeActivityDto;
import com.monew.monew_api.useractivity.dto.SubscribesActivityDto;
import com.monew.monew_api.useractivity.repository.UserActivityCacheCustomRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
@RequiredArgsConstructor
public class UserActivityCacheRepositoryImpl implements UserActivityCacheCustomRepository {

    private final MongoTemplate mongo;

    @Override
    public long incCommentLikeCount(Set<String> userIds, String commentId, int delta) {
        if (userIds.isEmpty()) return 0;

        var q1 = Query.query(where("_id").in(userIds).and("comments.id").is(commentId));
        var u1 = new Update()
                .inc("comments.$.likeCount", delta)
                .set("updatedAt", LocalDateTime.now());
        UpdateResult r1 = mongo.updateMulti(q1, u1, UserActivityCacheDocument.class);

        var q2 = Query.query(where("_id").in(userIds).and("commentLikes.commentId").is(commentId));
        var u2 = new Update().inc("commentLikes.$.commentLikeCount", delta)
                .set("updatedAt", LocalDateTime.now());
        UpdateResult r2 = mongo.updateMulti(q2, u2, UserActivityCacheDocument.class);

        return r1.getModifiedCount() + r2.getModifiedCount();
    }

    @Override
    public long incArticleViewCount(Set<String> userIds, String articleId, int delta) {
        if (userIds.isEmpty()) return 0;
        var q = Query.query(where("_id").in(userIds).and("articleViews.articleId").is(articleId));
        var u = new Update()
                .inc("articleViews.$.articleViewCount", delta)
                .set("updatedAt", LocalDateTime.now());
        return mongo.updateMulti(q, u, UserActivityCacheDocument.class).getModifiedCount();
    }

    @Override
    public long incArticleCommentCount(Set<String> userIds, String articleId, int delta) {
        if (userIds.isEmpty()) return 0;
        var q = Query.query(where("_id").in(userIds).and("articleViews.articleId").is(articleId));
        var u = new Update()
                .inc("articleViews.$.articleCommentCount", delta)
                .set("updatedAt", LocalDateTime.now());
        return mongo.updateMulti(q, u, UserActivityCacheDocument.class).getModifiedCount();
    }

    @Override
    public long pushCommentLike(String userId, CommentLikeActivityDto dto, int keepLatest) {
        var q = Query.query(where("_id").is(userId));
        var u = new Update()
                .push("commentLikes")
                .atPosition(0)
                .slice(keepLatest)
                .each(dto)
                .set("updatedAt", LocalDateTime.now());
        return mongo.updateFirst(q, u, UserActivityCacheDocument.class).getModifiedCount();
    }

    @Override
    public long pullCommentLike(String userId, String commentId) {
        var q = Query.query(where("_id").is(userId));
        var u = new Update()
                .pull("commentLikes", new BasicDBObject("commentId", commentId))
                .set("updatedAt", LocalDateTime.now());
        return mongo.updateFirst(q, u, UserActivityCacheDocument.class).getModifiedCount();
    }

    @Override
    public long pushComment(String userId, CommentActivityDto dto, int keepLatest) {
        var q = Query.query(where("_id").is(userId));
        var u = new Update()
                .push("comments")
                .atPosition(0)
                .slice(keepLatest)
                .each(dto)
                .set("updatedAt", LocalDateTime.now());
        return mongo.updateFirst(q, u, UserActivityCacheDocument.class).getModifiedCount();
    }

    @Override
    public long updateCommentContentForUsers(Set<String> userIds, String commentId, String newContent) {
        if (userIds.isEmpty()) return 0;

        var q1 = Query.query(where("_id").in(userIds).and("comments.id").is(commentId));
        var u1 = new Update()
                .set("comments.$.content", newContent)
                .set("updatedAt", LocalDateTime.now());
        var r1 = mongo.updateMulti(q1, u1, UserActivityCacheDocument.class);

        var q2 = Query.query(where("_id").in(userIds).and("commentLikes.commentId").is(commentId));
        var u2 = new Update()
                .set("commentLikes.$[l].commentContent", newContent)
                .set("updatedAt", LocalDateTime.now());
        u2.filterArray(where("l.commentId").is(commentId));
        var r2 = mongo.updateMulti(q2, u2, UserActivityCacheDocument.class);

        return r1.getModifiedCount() + r2.getModifiedCount();
    }


    @Override
    public long removeCommentEverywhere(Set<String> userIds, String commentId) {
        if (userIds.isEmpty()) return 0;
        var q = Query.query(where("_id").in(userIds));
        var u = new Update()
                .pull("comments", new BasicDBObject("id", commentId))
                .pull("commentLikes", new BasicDBObject("commentId", commentId))
                .set("updatedAt", LocalDateTime.now());
        return mongo.updateMulti(q, u, UserActivityCacheDocument.class).getModifiedCount();
    }

    @Override
    public long pushArticleView(String userId, ArticleViewActivityDto dto, int keepLatest) {
        var q = Query.query(where("_id").is(userId));
        var u = new Update()
                .push("articleViews")
                .atPosition(0)
                .slice(keepLatest)
                .each(dto)
                .set("updatedAt", LocalDateTime.now());
        return mongo.updateFirst(q, u, UserActivityCacheDocument.class).getModifiedCount();
    }

    @Override
    public long updateInterestKeywords(String interestId, List<String> newKeywords) {
        var q = Query.query(where("subscriptions.interestId").is(interestId));
        var u = new Update()
                .set("subscriptions.$[it].interestKeywords", newKeywords)
                .set("updatedAt", LocalDateTime.now());
        u.filterArray(where("it.interestId").is(interestId));
        return mongo.updateMulti(q, u, UserActivityCacheDocument.class).getModifiedCount();
    }

    @Override
    public long removeInterestEverywhere(Set<String> userIds, String interestId) {
        if (userIds.isEmpty()) return 0;

        Query q = Query.query(Criteria.where("_id").in(userIds));
        Update u = new Update()
                .pull("subscriptions", new BasicDBObject("interestId", interestId))
                .set("updatedAt", LocalDateTime.now());

        return mongo.updateMulti(q, u, UserActivityCacheDocument.class).getModifiedCount();
    }

    @Override
    public long addSubscription(String userId, SubscribesActivityDto dto) {
        var q = Query.query(where("_id").is(userId));

        if (dto.getId() != null) {
            var pullExisting = new Update()
                    .pull("subscriptions", Query.query(where("id").is(dto.getId())).getQueryObject())
                    .set("updatedAt", LocalDateTime.now());
            mongo.updateFirst(q, pullExisting, UserActivityCacheDocument.class);
        }

        var push = new Update()
                .push("subscriptions")
                .atPosition(0)
                .slice(10)
                .each(dto)
                .set("updatedAt", LocalDateTime.now());

        var result = mongo.updateFirst(q, push, UserActivityCacheDocument.class);
        return result.getModifiedCount();
    }

    @Override
    public long removeSubscription(String userId, String subscriptionId) {
        var q = Query.query(where("_id").is(userId));
        var u = new Update()
                .pull("subscriptions", Query.query(where("id").is(subscriptionId)).getQueryObject())
                .set("updatedAt", LocalDateTime.now());
        return mongo.updateFirst(q, u, UserActivityCacheDocument.class).getModifiedCount();
    }
}
