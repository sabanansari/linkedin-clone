package com.ansari.linkedin.posts_service.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostCreated {
    private Long ownerUserId;
    private Long postId;
    private Long userId;
    private  String content;
}
