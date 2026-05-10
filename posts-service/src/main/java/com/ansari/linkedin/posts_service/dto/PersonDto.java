package com.ansari.linkedin.posts_service.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PersonDto {

    private Long id;
    private Long userId; // Corresponding User ID from User Service

    private String name;

}
