package com.ansari.linkedin.posts_service.repository;

import com.ansari.linkedin.posts_service.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikesRepository extends JpaRepository<PostLike,Long> {

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    void deleteByUserIdAndPostId(long userId, Long postId);
}


