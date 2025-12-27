package com.paypal.notification_service.service;

import java.util.List;

import com.paypal.notification_service.entity.Notification;

public interface NotificationService {
    Notification sendNotification(Notification notification);
    List<Notification> getNotificationsByUserId(Long userId);
}
