package com.ansari.linkedin.posts_service.service;

import com.ansari.linkedin.posts_service.auth.AuthContextHolder;
import com.ansari.linkedin.posts_service.client.ConnectionsServiceClient;
import com.ansari.linkedin.posts_service.dto.PersonDto;
import com.ansari.linkedin.posts_service.dto.PostCreateRequestDto;
import com.ansari.linkedin.posts_service.dto.PostDto;
import com.ansari.linkedin.posts_service.entity.Post;
import com.ansari.linkedin.posts_service.event.PostCreated;
import com.ansari.linkedin.posts_service.exception.ResourceNotFoundException;
import com.ansari.linkedin.posts_service.repository.PostsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PostsService {

    private final PostsRepository postsRepository;
    private final ModelMapper modelMapper;
    private final ConnectionsServiceClient connectionsServiceClient;
    private final KafkaTemplate<Long, PostCreated> postCreatedKafkaTemplate;

    public PostDto createPost(PostCreateRequestDto postCreateRequestDto) {

        Long userId = AuthContextHolder.getCurrentUserId();
        Post post = modelMapper.map(postCreateRequestDto, Post.class);
        post.setUserId(userId);
        Post savedPost = postsRepository.save(post);

        List<PersonDto> personDtoList = connectionsServiceClient.getFirstDegreeConnections(userId,userId);

        if(personDtoList != null){
            for(PersonDto person: personDtoList){ //send notification to each connection
                log.info("found user with id: {} as a connection of user with id: {}", person.getUserId(), userId);
                postCreatedKafkaTemplate.send("post_created_topic", PostCreated.builder()
                        .postId(savedPost.getId())
                        .userId(person.getUserId())
                        .ownerUserId(userId)
                        .content(savedPost.getContent())
                        .build());
            }
        }



        return modelMapper.map(savedPost, PostDto.class);
    }

    public PostDto getPostById(Long postId) {
        log.debug("Retrieving post with id: {}", postId);
        Long userId = AuthContextHolder.getCurrentUserId();
        Post post = postsRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        return modelMapper.map(post, PostDto.class);
    }

    public List<PostDto> getAllPostsOfUser(Long userId) {
        log.debug("Retrieving all posts for user with id: {}", userId);
        List<Post> posts = postsRepository.findByUserId(userId);
        return posts.stream().map(post -> modelMapper.map(post, PostDto.class)).toList();
    }
}
