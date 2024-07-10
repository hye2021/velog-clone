package org.example.blog.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blog.entity.Post;
import org.example.blog.entity.Series;
import org.example.blog.entity.Tag;
import org.example.blog.entity.User;
import org.example.blog.service.PostService;
import org.example.blog.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final String PATH = "post/";

    // dependency injection
    private final PostService postService;
    private final UserService userService;

    @GetMapping("/write") // post editor page
    public String write(Model model) {
        model.addAttribute("post", new Post());
        return PATH + "write";
    }

    @PostMapping("/upload") // upload post
    public String upload(@ModelAttribute(name = "post") Post post,
                         @RequestParam(name = "str_tags", required = false, defaultValue = "") String strTags,
                         @RequestParam(name = "str_series", required = false, defaultValue = "") String newSeries,
                         RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 정보가 없습니다.");
            return "redirect:/error";
        }

        // User 설정
        User user = userService.getUsersByUsername(authentication.getName());
        post.setUser(user);

        // 태그 설정
        if(!strTags.isEmpty())
        {
            String[] tags = strTags.split(" ");
            for (String tag : tags) {
                if (!tag.startsWith("#"))
                    continue;
                log.info("*** tag: {}", tag);
                Tag newTag = new Tag();
                newTag.setName(tag);
                newTag.setUser(user);

                postService.saveTag(newTag);
                post.getTags().add(newTag);
            }
        } else {
            log.info("*** strTags is empty");
        }

        // todo: 임시글 여부
        post.setPublishStatus(false);

        // todo: 시리즈
        if (!newSeries.isEmpty()) {
            log.info("*** newSeries: {}", newSeries);
            Series series = new Series();
            series.setTitle(newSeries);
            series.setUser(user);
            postService.saveSeries(series);
            // post의 시리즈로 지정
            post.setSeries(series);
        }

        // todo: 썸네일

        postService.savePost(post);
        return "redirect:/"; // todo
    }
}
