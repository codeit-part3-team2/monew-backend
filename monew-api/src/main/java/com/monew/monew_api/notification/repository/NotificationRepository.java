package com.monew.monew_api.notification.repository;

import com.monew.monew_api.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {

    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Notification n SET n.confirmed = true, n.updatedAt = CURRENT_TIMESTAMP
            WHERE n.user.id = :userId AND n.confirmed = false
    """)
    int confirmAllByUserId(Long userId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Notification n WHERE n.confirmed = true AND n.updatedAt < :oneWeekAgo")
    int deleteAllOldConfirmed(LocalDateTime oneWeekAgo);
}
