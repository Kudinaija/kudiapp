package com.kudiapp.kudiapp.controller;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.request.notification.CreateNotificationRequest;
import com.kudiapp.kudiapp.services.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }


    @PostMapping("/create")
    public ResponseEntity<GenericResponse> create(@RequestBody CreateNotificationRequest request) {
        GenericResponse response = notificationService.createNotification(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PutMapping("/mark-read/{notificationId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<GenericResponse> markAsRead(@PathVariable Long notificationId) {
        GenericResponse response = notificationService.markAsRead(notificationId);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @GetMapping("/recipient/{recipient}")
    public ResponseEntity<GenericResponse> getNotifications(@PathVariable String recipient) {
        GenericResponse response = notificationService.getNotificationsForRecipient(recipient);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
