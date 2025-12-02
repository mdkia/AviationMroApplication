// Notification.java
package com.aviation.mro.modules.notification.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(nullable = false, length = 20)
    private String type;

    @Column(length = 20)
    private String priority = "NORMAL";

    @Column(name = "related_entity", length = 50)
    private String relatedEntity;

    @Column(name = "related_id")
    private Long relatedId;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "created_by", length = 100)
    private String createdBy;
}