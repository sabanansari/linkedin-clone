package com.ansari.linkedin.posts_service.repository;

import com.ansari.linkedin.posts_service.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface PostsRepository extends JpaRepository<Post,Long> {

    List<Post> findByUserId(Long userId);

}
