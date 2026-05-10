package com.ansari.linkedin.user_service.service;


import com.ansari.linkedin.user_service.dto.LoginRequestDto;
import com.ansari.linkedin.user_service.dto.SignUpRequestDto;
import com.ansari.linkedin.user_service.dto.UserDto;
import com.ansari.linkedin.user_service.entity.User;
import com.ansari.linkedin.user_service.event.UserCreatedEvent;
import com.ansari.linkedin.user_service.exception.BadRequestException;
import com.ansari.linkedin.user_service.exception.ResourceNotFoundException;
import com.ansari.linkedin.user_service.repository.UserRepository;
import com.ansari.linkedin.user_service.utils.PasswordUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JWTService jwtService;
    private final KafkaTemplate<Long, UserCreatedEvent> userCreatedEventKafkaTemplate;

    public UserDto signUp(SignUpRequestDto signUpRequestDto) {

        boolean exists = userRepository.existsByEmail(signUpRequestDto.getEmail());
        if (exists) {
            throw new BadRequestException("User already exists with email: " + signUpRequestDto.getEmail());
        }

        User user = modelMapper.map(signUpRequestDto, User.class);
        user.setPassword(PasswordUtil.hashPassword(signUpRequestDto.getPassword()));
        User savedUser = userRepository.save(user);

        UserCreatedEvent userCretedEvent = UserCreatedEvent.builder()
                .userId(user.getId())
                .name(user.getName())
                .build();

        userCreatedEventKafkaTemplate.send("user_created_topic",userCretedEvent);

        return modelMapper.map(savedUser, UserDto.class);
    }

    public String login(LoginRequestDto loginRequestDto) {

        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + loginRequestDto.getEmail()));

        boolean isPasswordValid = PasswordUtil.verifyPassword(loginRequestDto.getPassword(), user.getPassword());
        if (!isPasswordValid) {
            throw new BadRequestException("Invalid password");
        }

        return jwtService.generateAccessToken(user);

    }
}
