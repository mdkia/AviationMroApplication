// NotificationDto.java
package com.aviation.mro.modules.notification.domain.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationDto {
    private Long id;
    private String title;
    private String message;
    private String type;
    private String priority;
    private String relatedEntity;
    private Long relatedId;
    private LocalDateTime createdAt;
    private boolean isRead;
}