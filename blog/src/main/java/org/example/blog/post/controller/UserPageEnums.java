package org.example.blog.post.controller;

public enum UserPageEnums {
    POSTS("posts"),
    SERIES("series"),
    ABOUT("about");

    private final String page;

    UserPageEnums(String page) {
        this.page = page;
    }

    public String getPage() {
        return page;
    }

    public static boolean contains(String page) {
        for (UserPageEnums userPageEnum : UserPageEnums.values()) {
            if (userPageEnum.getPage().equals(page)) {
                return true;
            }
        }
        return false;
    }
}
