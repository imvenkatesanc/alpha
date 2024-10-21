package com.alpha.model;

import lombok.Data;

@Data
public class CommentRequestDto {
    private Long userId;
    private String content;

    // You can add validation annotations if needed, e.g. @NotBlank for content
}
