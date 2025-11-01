package com.monew.monew_api.notification.service;

import com.monew.monew_api.comments.event.CommentLikedEvent;
import com.monew.monew_api.common.dto.CursorPageResponse;
import com.monew.monew_api.common.exception.notification.NotificationAccessDeniedException;
import com.monew.monew_api.common.exception.notification.NotificationAlreadyConfirmedException;
import com.monew.monew_api.common.exception.notification.NotificationNotFoundException;
import com.monew.monew_api.domain.user.User;
import com.monew.monew_api.domain.user.repository.UserRepository;
import com.monew.monew_api.notification.dto.request.NotificationCursorPageRequest;
import com.monew.monew_api.notification.dto.response.NotificationDto;
import com.monew.monew_api.notification.entity.Notification;
import com.monew.monew_api.notification.enums.ResourceType;
import com.monew.monew_api.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createCommentLikeNotification(CommentLikedEvent event) {
        User commentAuthorIdOnly = userRepository.getReferenceById(event.commentAuthorId());

        String content = String.format("%s님이 나의 댓글을 좋아합니다.", event.likerNickname());

        notificationRepository.save(
                new Notification(
                        commentAuthorIdOnly,
                        content,
                        ResourceType.comment,
                        event.commentId()));
    }

    public CursorPageResponse<NotificationDto> getNonConfirmedNotifications(Long userId, NotificationCursorPageRequest cursorPageRequest) {
        return notificationRepository.findAllNonConfirmedNotifications(userId, cursorPageRequest);
    }

    @Transactional
    public void setOneConfirmed(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> {
                    log.warn("[알림 조회 실패] 존재하지 않는 알림: {}", notificationId);
                    return new NotificationNotFoundException();
                });

        if (!notification.getUser().getId().equals(userId)) {
            log.warn("[알림 확인 실패] 권한 없는 사용자: {}, 알림: {}", userId, notificationId);
            throw new NotificationAccessDeniedException();
        }

        if (notification.isConfirmed()) {
            log.warn("[알림 중복 확인] 이미 확인된 알림: {}", notificationId);
            throw new NotificationAlreadyConfirmedException();
        }

        notification.confirm();
    }

    @Transactional
    public void setAllConfirmed(Long userId) {
        int affectedRows = notificationRepository.confirmAllByUserId(userId);

        log.info("[알림 전체 확인] 사용자 ID: {}, 확인된 알림 개수: {}개", userId, affectedRows);
    }
}