package com.kudiapp.kudiapp.services.serviceImpl;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.request.notification.CreateNotificationRequest;
import com.kudiapp.kudiapp.models.Notification;
import com.kudiapp.kudiapp.repository.NotificationRepository;
import com.kudiapp.kudiapp.services.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public GenericResponse createNotification(CreateNotificationRequest request) {

        Notification notification = Notification.builder()
                .recipient(request.getRecipient())
                .type(request.getType())
                .title(request.getTitle())
                .message(request.getMessage())
                .metadata(request.getMetadata())
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();

        notificationRepository.save(notification);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Notification created")
                .httpStatus(HttpStatus.CREATED)
                .data(notification)
                .build();
    }

    @Override
    public GenericResponse markAsRead(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElse(null);

        if (notification == null) {
            return new GenericResponse(false, "Notification not found", HttpStatus.NOT_FOUND);
        }

        notification.setRead(true);
        notificationRepository.save(notification);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Notification marked as read")
                .httpStatus(HttpStatus.OK)
                .data(notification)
                .build();
    }

    @Override
    public GenericResponse getNotificationsForRecipient(String recipient) {

        List<Notification> notifications = notificationRepository.findByRecipient(recipient);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Notifications fetched")
                .httpStatus(HttpStatus.OK)
                .data(notifications)
                .build();
    }
}
