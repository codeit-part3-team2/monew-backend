package com.monew.monew_api.notification.service;

import com.monew.monew_api.comments.event.CommentLikedEvent;
import com.monew.monew_api.common.dto.CursorPageResponse;
import com.monew.monew_api.common.exception.notification.NotificationAccessDeniedException;
import com.monew.monew_api.common.exception.notification.NotificationAlreadyConfirmedException;
import com.monew.monew_api.common.exception.notification.NotificationNotFoundException;
import com.monew.monew_api.interest.entity.Interest;
import com.monew.monew_api.notification.dto.request.NotificationCursorPageRequest;
import com.monew.monew_api.notification.dto.response.NotificationDto;
import com.monew.monew_api.notification.entity.Notification;
import com.monew.monew_api.notification.enums.ResourceType;
import com.monew.monew_api.notification.repository.NotificationRepository;
import com.monew.monew_api.subscribe.entity.Subscribe;
import com.monew.monew_api.subscribe.repository.SubscribeRepository;
import com.monew.monew_api.user.User;
import com.monew.monew_api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SubscribeRepository subscribeRepository;

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

    @Transactional
    public void createInterestRegisteredNotification(Map<Long, Integer> countsByInterestId) {
//        Map<Long, Integer> countsByInterestId = event.newLinkCountsByInterestId(); // 기존 event 기반 처리시 사용
        Set<Long> interestIds = countsByInterestId.keySet();

        // 관심사를 구독하고 있는 구독자 조회
        List<Subscribe> subscriptions = subscribeRepository.findAllByInterestIds(interestIds);
        if (subscriptions.isEmpty()) {
            return;
        }

        // 관심사별 구독자 리스트 그룹핑
        Map<Long, List<User>> usersByInterestId = subscriptions.stream()
                .collect(Collectors.groupingBy(
                        subscribe -> subscribe.getInterest().getId(),
                        Collectors.mapping(Subscribe::getUser, Collectors.toList())
                ));

        // 관심사 ID - 관심사 명
        Map<Long, String> interestNameMap = subscriptions.stream()
                .map(Subscribe::getInterest)
                .distinct()
                .collect(Collectors.toMap(Interest::getId, Interest::getName));

        // 알림 생성
        List<Notification> newNotifications = new ArrayList<>();
        for (Long interestId : interestIds) {
            List<User> usersToNotify = usersByInterestId.get(interestId);
            int count = countsByInterestId.get(interestId);
            String interestName = interestNameMap.get(interestId);

            if (usersToNotify == null || usersToNotify.isEmpty() || count == 0) {
                continue;
            }

            String content = String.format("%s와 관련된 기사가 %d건 등록되었습니다.", interestName, count);
            for (User user : usersToNotify) {
                newNotifications.add(new Notification(
                        user,
                        content,
                        ResourceType.interest,
                        interestId
                ));
            }
        }

        if (!newNotifications.isEmpty()) {
            notificationRepository.saveAll(newNotifications);
            log.info("[관심사 알림 생성 성공] {}개의 알림 생성", newNotifications.size());
        }
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