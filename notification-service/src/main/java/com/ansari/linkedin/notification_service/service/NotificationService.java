package com.ansari.linkedin.notification_service.service;

import com.ansari.linkedin.notification_service.entity.Notification;
import com.ansari.linkedin.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void addNotification(Notification notification) {
       log.info("Adding  with content: {}", notification.getMessage());
        notification = notificationRepository.save(notification);

        //SendMailer to send email
        //FCM - for mobie sms
    }


}
