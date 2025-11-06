package com.monew.monew_api.notification.repository;

import com.monew.monew_api.common.config.QuerydslConfig;
import com.monew.monew_api.user.User;
import com.monew.monew_api.notification.entity.Notification;
import com.monew.monew_api.notification.enums.ResourceType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslConfig.class)
class NotificationRepositoryTest {

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    EntityManager entityManager;

    private User user1;
    private User user2;

    @BeforeEach
    void setup() {
        user1 = new User("user1@example.com", "테스트 유저", "1234");
        entityManager.persist(user1);
        entityManager.flush();

        user2 = new User("user2@example.com", "테스트 유저 2", "1234");
        entityManager.persist(user2);
        entityManager.flush();
    }

    @Test
    @DisplayName("확인한지 일주일 경과된 알림은 삭제된다.")
    void deleteAllOldConfirmedTest() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekAgo = now.minusWeeks(1);
        LocalDateTime eightDaysAgo = now.minusDays(8);
        LocalDateTime sixDaysAgo = now.minusDays(6);

        Notification toDelete = createAndPersistNotification(user1, "삭제 대상 알림", true, eightDaysAgo.minusDays(1), eightDaysAgo);
        Notification keepConfirmed = createAndPersistNotification(user1, "유지 대상 (확인 됨)", true, oneWeekAgo, sixDaysAgo);
        Notification keepUnconfirmed = createAndPersistNotification(user1, "유지 대상 (미확인)", false, eightDaysAgo, eightDaysAgo);
        Notification toDelete2 = createAndPersistNotification(user2, "삭제 대상 (다른 사용자 알림)", true, eightDaysAgo, eightDaysAgo);

        entityManager.flush();
        entityManager.clear();

        // When
        int deletedCount = notificationRepository.deleteAllOldConfirmed(oneWeekAgo);

        // Then
        assertThat(deletedCount).isEqualTo(2);

        assertThat(notificationRepository.findById(toDelete.getId()).isEmpty());
        assertThat(notificationRepository.findById(toDelete2.getId()).isEmpty());

        assertThat(notificationRepository.findById(keepConfirmed.getId()).isPresent());
        assertThat(notificationRepository.findById(keepUnconfirmed.getId()).isPresent());

        assertThat(notificationRepository.count()).isEqualTo(2);
    }

    private Notification createAndPersistNotification(User user, String content, boolean confirmed, LocalDateTime createdAt, LocalDateTime updatedAt) {

        Notification notification = new Notification(user, content, ResourceType.interest, 1L);

        if (confirmed) {
            notification.confirm();
        }

        notificationRepository.save(notification);
        entityManager.flush();

        int updatedRows = entityManager.createQuery("UPDATE Notification n SET n.createdAt = :createdAt, n.updatedAt = :updatedAt WHERE n.id = :id")
                .setParameter("createdAt", createdAt)
                .setParameter("updatedAt", updatedAt)
                .setParameter("id", notification.getId())
                .executeUpdate();

        assertThat(updatedRows).isEqualTo(1);

        return notification;
    }

}