package com.ansari.linkedin.posts_service.service;

import com.ansari.linkedin.posts_service.auth.AuthContextHolder;
import com.ansari.linkedin.posts_service.entity.Post;
import com.ansari.linkedin.posts_service.entity.PostLike;
import com.ansari.linkedin.posts_service.event.PostLiked;
import com.ansari.linkedin.posts_service.exception.BadRequestException;
import com.ansari.linkedin.posts_service.exception.ResourceNotFoundException;
import com.ansari.linkedin.posts_service.repository.LikesRepository;
import com.ansari.linkedin.posts_service.repository.PostsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LikeService {

    private final LikesRepository likesRepository;
    private final PostsRepository postsRepository;
    private final KafkaTemplate<Long, PostLiked> postLikedKafkaTemplate;

    public void likePost(Long postId){

        Long userId = AuthContextHolder.getCurrentUserId();
        log.info("Attempting to like the post with id: {} by user with id: {}", postId, userId);

        Post post = postsRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        boolean alreadyLiked = likesRepository.existsByUserIdAndPostId(userId, postId);

        if(alreadyLiked){
            throw new BadRequestException("User already liked the post with id: "+postId);
        }

        PostLike postLike = new PostLike();
        postLike.setUserId(userId);
        postLike.setPostId(postId);
        likesRepository.save(postLike);

        postLikedKafkaTemplate.send("post_liked_topic", PostLiked.builder()
                .postId(postId)
                .likedByUserId(userId)
                .ownerUserId(post.getUserId())
                .build());

        log.info("Post with id: {} liked by user with id: {}", postId, userId);
    }

    @Transactional
    public void unlikePost(Long postId) {

        Long userId = AuthContextHolder.getCurrentUserId();

        log.info("Attempting to like the post with id: {} by user with id: {}", postId, userId);

        boolean exists = postsRepository.existsById(postId);
        if(!exists){
            throw new ResourceNotFoundException("Post not found with id: "+postId);
        }

        boolean alreadyLiked = likesRepository.existsByUserIdAndPostId(userId, postId);

        if(!alreadyLiked){
            throw new BadRequestException("Cannot unlike the post which is not liked: "+postId);
        }

        likesRepository.deleteByUserIdAndPostId(userId,postId);

        log.info("Post with id: {} unliked by user with id: {}", postId, userId);


    }
}
