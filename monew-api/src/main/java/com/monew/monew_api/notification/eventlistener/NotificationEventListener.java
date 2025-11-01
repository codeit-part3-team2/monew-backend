package com.monew.monew_api.notification.eventlistener;

import com.monew.monew_api.comments.event.CommentLikedEvent;
import com.monew.monew_api.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @Async
    @EventListener(CommentLikedEvent.class)
    public void handleCommentLiked(CommentLikedEvent event) {
        try {
            notificationService.createCommentLikeNotification(event);
        } catch (Exception e) {
            log.error("알림 생성 실패: {}", event.commentId(), e);
        }
    }
}
