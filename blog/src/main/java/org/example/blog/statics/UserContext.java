package org.example.blog.statics;

public class UserContext {
    /* Thread Local
    *
    * 각 스레드에 고유한 변수를 유지하기 위해 사용된다.
    * 웹 어플리케이션에서는 각 요청마다 별도의 스레드가 생성된다.
    * 따라서 각 요청에 따라 고유한 데이터를 저장하는데 사용된다.
    */
    private static final ThreadLocal<String> currentUser = new ThreadLocal<>();

    public static void setCurrentUser(String username) {
        currentUser.set(username);
    }

    public static String getCurrentUser() {
        return currentUser.get();
    }

    public static boolean isLogin() {
        return currentUser.get() != null;
    }

    public static void clear() {
        currentUser.remove();
    }
}
