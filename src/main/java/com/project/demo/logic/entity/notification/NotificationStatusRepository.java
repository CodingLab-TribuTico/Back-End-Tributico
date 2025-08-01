package com.project.demo.logic.entity.notification;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationStatusRepository extends JpaRepository<UserNotificationStatus, Long> {
    List<UserNotificationStatus> findByUserIdAndIsReadFalse(Long userId);
    Optional<UserNotificationStatus> findByUserIdAndNotificationId(Long userId, Long notificationId);
    List<UserNotificationStatus> findByNotificationId(Long notificationId);
    void deleteByNotificationId(Long notificationId);
}
