package com.kudiapp.kudiapp.services;


import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.request.notification.CreateNotificationRequest;

public interface NotificationService {

    GenericResponse createNotification(CreateNotificationRequest request);

    GenericResponse markAsRead(Long notificationId);

    GenericResponse getNotificationsForRecipient(String recipient);
}
