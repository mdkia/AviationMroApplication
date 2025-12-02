// NotificationController.java
package com.aviation.mro.modules.notification.controller;

import com.aviation.mro.modules.notification.domain.dto.NotificationDto;
import com.aviation.mro.modules.notification.domain.model.Notification;
import com.aviation.mro.modules.notification.domain.model.UserNotification;
import com.aviation.mro.modules.notification.repository.NotificationRepository;
import com.aviation.mro.modules.notification.repository.UserNotificationRepository;
import com.aviation.mro.modules.notification.service.NotificationService;
import com.aviation.mro.shared.common.ApiResponse;
import com.aviation.mro.shared.security.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "سیستم اعلانات داخلی")
public class NotificationController {

    private final UserNotificationRepository userNotificationRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse> getMyNotifications() {
        Long userId = SecurityUtils.getCurrentUser().getId();

        List<UserNotification> userNotifs = userNotificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId);

        List<NotificationDto> dtos = userNotifs.stream()
                .map(un -> {
                    Notification n = notificationRepository.findById(un.getNotificationId())
                            .orElse(new Notification());
                    NotificationDto dto = new NotificationDto();
                    dto.setId(n.getId());
                    dto.setTitle(n.getTitle());
                    dto.setMessage(n.getMessage());
                    dto.setType(n.getType());
                    dto.setPriority(n.getPriority());
                    dto.setRelatedEntity(n.getRelatedEntity());
                    dto.setRelatedId(n.getRelatedId());
                    dto.setCreatedAt(n.getCreatedAt());
                    dto.setRead(un.isRead());
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(ApiResponse.success("دریافت اعلانات", dtos));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse> getUnreadCount() {
        Long userId = SecurityUtils.getCurrentUser().getId();
        long count = userNotificationRepository.countByUserIdAndIsReadFalse(userId);
        return ResponseEntity.ok(ApiResponse.success("تعداد اعلانات خوانده نشده", count));
    }

    @PostMapping("/read/{notificationId}")
    public ResponseEntity<ApiResponse> markAsRead(@PathVariable Long notificationId) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        UserNotification un = userNotificationRepository
                .findByUserIdAndNotificationId(userId, notificationId);
        if (un != null) {
            un.setRead(true);
            un.setReadAt(LocalDateTime.now());
            userNotificationRepository.save(un);
        }
        return ResponseEntity.ok(ApiResponse.success("اعلان خوانده شد"));
    }

    @PostMapping("/test-send")
    public ResponseEntity<ApiResponse> testSendNotification() {
        Long currentUserId = SecurityUtils.getCurrentUser().getId();

        notificationService.sendToUsers(
                List.of(currentUserId),  // فقط به خودت بفرست
                "تست نوتیفیکیشن",
                "این یک پیام آزمایشی از Postman است!",
                "INFO",
                "HIGH",
                "TEST",
                999L
        );

        return ResponseEntity.ok(ApiResponse.success("نوتیفیکیشن تست ارسال شد"));
    }
}