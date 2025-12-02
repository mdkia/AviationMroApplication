package com.aviation.mro.modules.notification.repository;

import com.aviation.mro.modules.notification.domain.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
