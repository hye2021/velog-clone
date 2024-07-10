package org.example.blog.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class PostDto {
    @NotEmpty(message = "title is required")
    private String title;
    private String tags; // #tag를 " "으로 구분한 문자열
    private String content;
    private Long SeriesId;
    private String newSeriesTitle;
    private boolean publishStatus;
    private MultipartFile thumbnail;
}
