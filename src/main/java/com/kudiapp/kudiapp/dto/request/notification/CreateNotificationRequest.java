package com.kudiapp.kudiapp.dto.request.notification;

import lombok.Data;

import java.util.Map;

@Data
public class CreateNotificationRequest {

    private String recipient;
    private String type;
    private String title;
    private String message;

    private Map<String, String> metadata;
}
