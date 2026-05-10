package com.ansari.linkedin.notification_service.consumer;

import com.ansari.linkedin.notification_service.entity.Notification;
import com.ansari.linkedin.notification_service.service.NotificationService;
import com.ansari.linkedin.posts_service.event.PostCreated;
import com.ansari.linkedin.posts_service.event.PostLiked;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostsConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "post_created_topic")
    public void handlePostCreated(PostCreated postCreated) {
        log.info("received notification for post created event: {}", postCreated);
         // Here you would typically save the notification to a database or send it to a notification service

        String message = String.format("your connection with user id: %d has created a new post with content: %s", postCreated.getOwnerUserId(), postCreated.getContent());

        Notification notification = Notification.builder()
                .message(message)
                .userId(postCreated.getUserId())
                .build();

        notificationService.addNotification(notification);

    }

    @KafkaListener(topics = "post_liked_topic")
    public void handlePostLiked(PostLiked postLiked) {
        log.info("received notification for post liked event: {}", postLiked);
        // Here you would typically save the notification to a database or send it to a notification service

        String message = String.format("your connection with user id: %d has liked your post with id: %d", postLiked.getLikedByUserId(),postLiked.getPostId());

        Notification notification = Notification.builder()
                .message(message)
                .userId(postLiked.getOwnerUserId())
                .build();

        notificationService.addNotification(notification);

    }
}
