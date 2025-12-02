package com.aviation.mro.modules.notification.service;

import com.aviation.mro.modules.auth.repository.UserRepository;
import com.aviation.mro.modules.notification.domain.model.Notification;
import com.aviation.mro.modules.notification.domain.model.UserNotification;
import com.aviation.mro.modules.notification.repository.NotificationRepository;
import com.aviation.mro.modules.notification.repository.UserNotificationRepository;
import com.aviation.mro.shared.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void sendToUsers(List<Long> userIds, String title, String message,
                            String type, String priority,
                            String relatedEntity, Long relatedId) {

        if (userIds == null || userIds.isEmpty()) return;

        String currentUsername = SecurityUtils.getCurrentUsername();

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setPriority(priority);
        notification.setRelatedEntity(relatedEntity);
        notification.setRelatedId(relatedId);
        notification.setCreatedBy(currentUsername);
        notificationRepository.save(notification);

        List<UserNotification> links = userIds.stream()
                .map(userId -> {
                    UserNotification un = new UserNotification();
                    un.setUserId(userId);
                    un.setNotificationId(notification.getId());
                    return un;
                })
                .toList();

        userNotificationRepository.saveAll(links);
    }

    // مثال آماده برای استفاده در ماژول Quality
    public void notifyNewNonConformance(String ncCode, String severity, Long ncId) {
        List<Long> recipients = userRepository.findUserIdsByRoleIn(List.of("INSPECTOR", "ADMIN"));
        sendToUsers(recipients,
                "عدم انطباق جدید گزارش شد",
                "کد: " + ncCode + " | شدت: " + severity,
                "ERROR", "URGENT", "NON_CONFORMANCE", ncId);
    }
}