package org.example.blog.service;

import org.example.blog.entity.Post;
import org.example.blog.entity.Tag;
import org.example.blog.repository.PostRepository;
import org.example.blog.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class PostService {
    @Autowired private PostRepository postRepository;
    @Autowired private TagRepository tagRepository;

    public Post savePost(Post post) {
        post.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        return postRepository.save(post);
    }

    public Tag saveTag(Tag tag) {
        return tagRepository.save(tag);
    }
}
