package com.kudiapp.kudiapp.models;

import com.kudiapp.kudiapp.models.baseclass.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_notification_recipient", columnList = "recipient"),
                @Index(name = "idx_notification_type", columnList = "type"),
                @Index(name = "idx_notification_is_read", columnList = "is_read"),
                @Index(name = "idx_notification_recipient_is_read", columnList = "recipient, is_read")
        }
)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Notification extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String recipient;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 2000)
    private String message;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @ElementCollection
    @CollectionTable(
            name = "notification_metadata",
            joinColumns = @JoinColumn(name = "notification_id"),
            indexes = {
                    @Index(name = "idx_notification_metadata_notification_id", columnList = "notification_id"),
                    @Index(name = "idx_notification_metadata_key", columnList = "meta_key")
            }
    )
    @MapKeyColumn(name = "meta_key")
    @Column(name = "meta_value", length = 500)
    private Map<String, String> metadata;
}
