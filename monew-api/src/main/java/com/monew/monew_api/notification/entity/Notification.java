package com.monew.monew_api.notification.entity;

import com.monew.monew_api.common.entity.BaseTimeEntity;
import com.monew.monew_api.user.User;
import com.monew.monew_api.notification.enums.ResourceType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notifications")
@Entity
public class Notification extends BaseTimeEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 100)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType resourceType;

    @Column(nullable = false)
    private Long resourceId;

    @Column(nullable = false)
    private boolean confirmed;

    public Notification(User user, String content, ResourceType resourceType, Long resourceId) {
        this.user = user;
        this.content = content;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public void confirm() {
        this.confirmed = true;
    }
}
