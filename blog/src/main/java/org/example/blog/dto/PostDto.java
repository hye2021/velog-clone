package org.example.blog.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PostDto {
    // @NotEmpty(message = "title is required")
    private String title;
    private String tags; // #tag를 " "으로 구분한 문자열
    private String content;
    private Long seriesId;
    private String newSeriesTitle;
    private boolean publishStatus;
    private MultipartFile thumbnail;
}
