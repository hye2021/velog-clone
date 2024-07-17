package org.example.blog.post.dto;

import lombok.Getter;
import org.example.blog.post.entity.Post;

@Getter
public class PostCardDto {
    private Long id;
    private String title;
    private String username;
    private String updateTime;
    private String thumbnailPath;

    public PostCardDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.username = post.getUser().getUsername();
        this.updateTime = post.getUpdateTime().toString();
        this.thumbnailPath = post.getThumbnailPath();
    }
}
