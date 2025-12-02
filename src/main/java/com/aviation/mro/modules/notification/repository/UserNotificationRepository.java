package com.aviation.mro.modules.notification.repository;

import com.aviation.mro.modules.notification.domain.model.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

    List<UserNotification> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByUserIdAndIsReadFalse(Long userId);

    @Query("SELECT un FROM UserNotification un WHERE un.userId = :userId AND un.notificationId = :notifId")
    UserNotification findByUserIdAndNotificationId(Long userId, Long notifId);
}
