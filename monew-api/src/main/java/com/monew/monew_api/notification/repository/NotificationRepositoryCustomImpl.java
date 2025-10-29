package com.monew.monew_api.notification.repository;

import com.monew.monew_api.common.dto.CursorPageResponse;
import com.monew.monew_api.notification.dto.request.NotificationCursorPageRequest;
import com.monew.monew_api.notification.dto.response.NotificationDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.monew.monew_api.notification.entity.QNotification.notification;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryCustomImpl implements NotificationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public CursorPageResponse<NotificationDto> findAllNonConfirmedNotifications(Long userId, NotificationCursorPageRequest cursorPageRequest) {
        List<NotificationDto> results = queryFactory
                .select(Projections.constructor(NotificationDto.class,
                        notification.id,
                        notification.createdAt,
                        notification.updatedAt,
                        notification.confirmed,
                        notification.user.id,
                        notification.content,
                        notification.resourceType,
                        notification.resourceId))
                .from(notification)
                .where(
                        notification.user.id.eq(userId),
                        notification.confirmed.isFalse(),
                        cursorPredicate(cursorPageRequest.cursor(), cursorPageRequest.after())
                )
                .orderBy(notification.createdAt.desc(), notification.id.asc())
                .limit(cursorPageRequest.limit() + 1)
                .fetch();

        Long totalCountTemp = queryFactory
                .select(notification.count())
                .from(notification)
                .where(notification.user.id.eq(userId).and(notification.confirmed.isFalse()))
                .fetchOne();

        long totalElements = totalCountTemp != null ? totalCountTemp : 0;

        if (results.size() <= cursorPageRequest.limit()) {
            return new CursorPageResponse<>(results, null, null, results.size(), totalElements, false);
        }

        results.remove(results.size() - 1);
        NotificationDto last = results.get(results.size() - 1);

        return new CursorPageResponse<>(
                results,
                String.valueOf(last.id()),
                last.createdAt(),
                results.size(),
                totalElements,
                true
        );
    }

    private BooleanExpression cursorPredicate(String cursor, LocalDateTime after) {
        if (cursor == null || cursor.isBlank() || after == null) {
            return null;
        }

        return (notification.createdAt.eq(after).and(notification.id.gt(Long.parseLong(cursor))))
                .or(notification.createdAt.lt(after));
    }
}
