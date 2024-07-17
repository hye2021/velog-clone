package org.example.blog.post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreateDto {
    // @NotEmpty(message = "title is required")
    private String title;
    private String tags; // #tag를 " "으로 구분한 문자열
    private String content;
    private Long seriesId;
    private String newSeriesTitle;
    private boolean publishStatus;
    private String thumbnail;
}
